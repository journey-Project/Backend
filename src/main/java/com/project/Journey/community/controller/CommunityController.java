package com.project.Journey.community.controller;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.project.Journey.companion.exception.PostException;
import com.project.Journey.community.dto.*;
import com.project.Journey.community.service.CommunityService;
import com.project.Journey.login.auth.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "커뮤니티 게시글 관리", description = "커뮤니티 관련 API")
public class CommunityController {
    private final CommunityService communityService;


    @Operation(summary = "커뮤니티 게시글 생성", description = """
            JSON 데이터와 이미지 파일을 포함하여 새 커뮤니티 게시글을 생성합니다.
            
            request 예시 : form-data -> key : data, value :{
            "memberId": 1,
            "country": "국내",
            "title": "요즘 제주도 날씨는 어떤가요?",
            "content": "저번주에 다녀온 지인 얘기 들어보니까 서울보다는 따뜻하다고 그러더라고요. 서울은 너무 요즘 너무 추운데 패딩은 입기 싫고 어떤 걸 입어야 할 지 고민중입니다... 지금 제주도에 계신 분들은 어떻게 입고 갔는지 궁금해서 여쭤봅니다!"
            },  key : images, value = 이미지파일
           
            """)
    @PostMapping(value = "/api/community/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Long> createPost(

            @Parameter(description = "커뮤니티 게시글 데이터 (JSON 문자열)")
            @RequestPart("data") String data,

            @Parameter(description = "이미지 파일들", required = false)
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        if (data == null) {
            throw new IllegalArgumentException("요청 데이터가 올바르지 않습니다.");
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            CommunityRequestDTO communityRequestDTO = objectMapper.readValue(data, CommunityRequestDTO.class);

            log.info("Request DTO: {}", communityRequestDTO);
            log.info("Received {} images", (images != null ? images.size() : 0));
            Long savedPostId = communityService.saveCommunityPost(communityRequestDTO, images);

            return ResponseEntity.ok(savedPostId);
        } catch (Exception e){
            log.error("게시글 저장 중 오류", e);
            throw new PostException("게시글 저장 중 오류가 발생했습니다.", HttpStatus.BAD_REQUEST);
        }

    }

    // 특정 게시글 조회 (조회수 증가 반영)
    @Operation(summary = "게시글 단건 조회", description = """
    게시글 ID를 통해 게시글 상세 정보를 조회합니다.
    
    API 요청 예시
    http://localhost:8080/api/community/getPostByPostId/{communityPostId}
    
    response 예시 :
    
            response : {
                "loginID": "traveler33",
                "nickname": "구름",
                "country": "국내",
                "title": "요즘 제주도 날씨는 어떤가요?",
                "content": "저번주에 다녀온 지인 얘기 들어보니까 서울보다는 따뜻하다고 그러더라고요.
            서울은 너무 요즘 너무 추운데 패딩은 입기 싫고 어떤 걸 입어야 할 지 고민중입니다...
            지금 제주도에 계신 분들은 어떻게 입고 갔는지 궁금해서 여쭤봅니다!",
                "view_count": 999,
                "comment_count": 0,
                "created_at": "2025-03-21T01:17:38.896685",
                "updated_at": "2025-03-21T01:17:38.896685",
                "profileImageUrl": null,
                "imageUrls": [
                    "이미지1.jpg",
                    "이미지2.jpg"
                ],
                "communityPostId": 39
            }
    
    """)
    @GetMapping("/api/community/getPostByPostId/{communityPostId}")
    public CommunityResponseDTO getCommunityPost(
            @PathVariable Long communityPostId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long currentMemberId = (userDetails != null) ? userDetails.getMember().getId() : null;
        return communityService.getPostByCommunityPostId(communityPostId, currentMemberId);
    }

    //오늘의 핫 게시물 기능 (페이지네이션 추가)
    @Operation(summary = "오늘의 핫 게시글 조회", description = """
            조회수가 높은 게시글을 페이지네이션하여 가져옵니다.
            
            API 호출 예시 : http://localhost:8080/api/community/hot-posts?page=1&size=3
            
            response 예시 : {
              "totalCount": 182,
              "currentPage": 1,
              "posts": [
                {
                  "country": "일본",
                  "title": "오사카 3박 4일 너무 즐거웠어요!",
                  "createdAt": "2025-05-02",
                  "communityPostId": 353
                },
                {
                  "country": "미국",
                  "title": "다들 환전은 어떻게 해서 가시나요?",
                  "createdAt": "2025-04-13",
                  "communityPostId": 1
                },
                {
                  "country": "프랑스",
                  "title": "날씨가 너무 좋아서 행복했어요..",
                  "createdAt": "2025-04-11",
                  "communityPostId": 171
                }
              ]
            }
            
            
            """)
    @GetMapping("/api/community/hot-posts")
    public ResponseEntity<Map<String, Object>> getHotPosts(
            @Parameter(description = "페이지 번호", example = "1")
            @RequestParam(defaultValue = "1") int page,

            @Parameter(description = "페이지당 게시글 수", example = "3")
            @RequestParam(defaultValue = "3") int size) {

        Map<String, Object> response = communityService.getHotPosts(page, size);
        return ResponseEntity.ok(response);
    }




    //게시글 수정
    @Operation(summary = "게시글 수정", description = """ 
            커뮤니티 게시글 ID에 해당하는 게시글을 수정합니다.
            
            1. localhost:8080/api/community/getPostByPostId/{communityPostId}로 업데이트 할 게시글을 불러옴
            
            2. http://localhost:8080/api/community/update/{CommunityPostId} 로 post라는 key값으로 
            value 값을 JSON 문자열을 넣고 새로운 이미지를 추가하고 싶으면 newImages라는 key값으로 value를 이미지파일로 추가해줍니다.(여러개 가능) 
            만약 삭제하고 싶은 이미지가 있으면 1.에서 불러온 imageUrls 리스트에서 삭제할 url을 지워주고 api를 호출해주면 됩니다.                                           그리고 JSON 문자열은 country, title, content 내용만 수정이 가능합니다!
            
            
            request 예시{
                "nickname": "커뮤니티테스트유저",
                "country": "국내",
                "title": "두번째변경한테스트제목~~~~~",
                "content": "두번째 변경한내용입니다",
                "view_count": 1,
                "comment_count": 0,
                "created_at": "2025-04-22T23:44:59.781954",
                "updated_at": "2025-04-22T23:44:59.781954",
                "profileImageUrl": null,
                "imageUrls": [
                    "이미지.jpg"
                ],
                "communityPostId": 350
            }
            """)
    @PutMapping("/api/community/update/{CommunityPostId}")
    public ResponseEntity<Void> updatePost(
            @Parameter(description = "커뮤니티 게시글 ID")
            @PathVariable Long CommunityPostId,

            @Parameter(description = "수정할 게시글 데이터 (JSON 문자열)")
            @RequestPart("post") String postJson,

            @Parameter(description = "새 이미지 파일들", required = false)
            @RequestPart(value = "newImages", required = false) List<MultipartFile> newImages) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            //LocalDateTime 처리 가능하도록 설정
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            CommunityResponseDTO dto = mapper.readValue(postJson, CommunityResponseDTO.class);

            communityService.updateCommunityPostById(CommunityPostId, dto, newImages);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }


    //게시글 삭제
    @Operation(summary = "게시글 삭제", description = "커뮤니티 게시글 ID에 해당하는 게시글을 삭제합니다.")
    @DeleteMapping("/api/community/DeletePosts/{CommunityPostId}")
    public ResponseEntity<Void> deleteCommunityPost(
            @Parameter(description = "커뮤니티 게시글 ID")
            @PathVariable Long CommunityPostId){
        communityService.deleteCommunityPost(CommunityPostId);
        return ResponseEntity.noContent().build();
    }

    //메인 페이지 - 오늘은 어떤 이야기를 나누었을까요? (조회수가 가장 높은 게시글 count 수만큼 반환)
    @Operation(summary = "메인 핫 게시글 조회", description = """
            조회수가 가장 높은 게시글을 count 개수만큼 반환합니다.
API 호출 예시 :
가져올 게시글들의 개수(count)를 지정하고 호출합니다.             
http://localhost:8080/api/community/main-hot-posts?count=3
        
response 예시 : 
[
{
"postId": 8,
"nickname": "바다",
"profileImageUrl":"image.jpg",
"imageUrls": [
"이미지4.jpg",
"이미지3.jpg"
],
"content": "오늘 난바역 근처에 있는 타코야끼 집에서 타코야끼를 먹었는데 너무 맛있었어요, 타코야끼 먹으니까  일본온 느낌도 나고 너무 좋네요",
"country": "일본"
},
{
"postId": 1,
"nickname": "user1234",
"profileImageUrl": "",
"imageUrls": [],
"content": "이탈리아로 떠나요!",
"country": "이탈리아"
},
{
"postId": 39,
"nickname": "테스트유저2",
"profileImageUrl": null,
"imageUrls": [
"이미지1.jpg",
"이미지2.jpg"
],
"content": "내용 입력 테스트2",
"country": "국내"
}
]

            """)
    @GetMapping("/api/community/main-hot-posts")
    public ResponseEntity<List<CommunityMainHotPostDTO>> getHotPosts(
            @Parameter(description = "반환할 게시글 수", example = "3")
            @RequestParam(defaultValue = "3") int count) {
        List<CommunityMainHotPostDTO> hotPosts = communityService.getHotPosts(count);
        return ResponseEntity.ok(hotPosts);
    }

    //검색 API 개발

    @Operation(summary = "커뮤니티 게시글 검색(페이지네이션)", description = """
            
            국가(커뮤니티), 제목, 작성자, 여행 기간(여행 시작일, 종료일)의 조건으로 게시글을 검색합니다.
           
            API 호출 예시: 
                       
            * API 테스트 default: http://localhost:8080/api/community/search?country=국내
            
            * 전체 게시글 검색 : http://localhost:8080/api/community/search
            
            * 게시글 제목으로 검색 : http://localhost:8080/api/community/search?country=국내&title=맛집
            
            * 작성자로 검색 : http://localhost:8080/api/community/search?country=국내&writer=구름
            
            * 시작일~종료일로 검색 : http://localhost:8080/api/community/search?country=국내&starteDate=2025-03-01&endDate=2025-04-01
            
            * 페이지, 게시글 갯수 지정 후 검색 :  http://localhost:8080/api/community/search?country=국내&page=1&recordSize=12
           
            response 예시 : 
            
            {
              "communityList": [
                {
                  "communityPostId": 353,
                  "title": "s3삭제테스트입니다",
                  "nickname": "테스트테스트",
                  "createdAt": "2025-05-02"
                },
                {
                  "communityPostId": 349,
                  "title": "에어비엔비가 나을까요?",
                  "nickname": "강아지",
                  "createdAt": "2025-04-20"
                },
                {
                  "communityPostId": 348,
                  "title": "가족끼리 갈만한 국내 여행지 추천해주세요",
                  "nickname": "마요네즈",
                  "createdAt": "2025-04-20"
                },
                {
                  "communityPostId": 347,
                  "title": "속초 좋네요~",
                  "nickname": "사랑",
                  "createdAt": "2025-04-20"
                },
                {
                  "communityPostId": 346,
                  "title": "이번주 강릉 날씨 많이 춥겠죠ㅜㅜ?.",
                  "nickname": "당근",
                  "createdAt": "2025-04-20"
                },
                {
                  "communityPostId": 345,
                  "title": "서울로 여행가는데 종로 맛집 추천해주세요!",
                  "nickname": "인형",
                  "createdAt": "2025-04-20"
                },
                {
                  "communityPostId": 344,
                  "title": "금요일 오전 7시 제주행 에어부산이요....",
                  "nickname": "사탕",
                  "createdAt": "2025-04-20"
                },
                {
                  "communityPostId": 343,
                  "title": "다음주에 부산 가는데 너무 설레요~",
                  "nickname": "바다",
                  "createdAt": "2025-04-20"
                },
                {
                  "communityPostId": 342,
                  "title": "방콕 여행 갈려구요~",
                  "nickname": "테스트유저22",
                  "createdAt": "2025-04-19"
                },
                {
                  "communityPostId": 341,
                  "title": "하와이 여행 갈려구요~",
                  "nickname": "테스트유저",
                  "createdAt": "2025-04-19"
                },
                {
                  "communityPostId": 340,
                  "title": "국내 여행지 추천",
                  "nickname": "산",
                  "createdAt": "2025-04-19"
                },
                {
                  "communityPostId": 339,
                  "title": "속초 날씨는 어떤가요?",
                  "nickname": "속초러버",
                  "createdAt": "2025-04-16"
                }
              ],
              "pagination": {
                "totalRecordCount": 98, -> 전체 데이터 수
                "totalPageCount": 9, -> 전체 페이지 수
                "startPage": 1, -> 첫 페이지 번호
                "endPage": 9, -> 끝 페이지 번호
                "limitStart": 0, -> LIMIT 시작 위치
                "existPrevPage": false, -> 이전 페이지 존재 여부
                "existNextPage": true -> 다음 페이지 존재 여부
              }
            }
      
            """)
    @GetMapping("/api/community/search")
    public CommunitySearchResponseDTO searchPosts(
            //기본은 country 값을 지정(default), 커뮤니티 게시글 전체를 조회하고 싶으면 쿼리파라미터 없이 호출
            @Parameter(description = "국가", required = false)
            @RequestParam(required = false) String country,

            @Parameter(description = "게시글 번호", required = false)
            @RequestParam(required = false) Long number,

            @Parameter(description = "게시글 제목", required = false)
            @RequestParam(required = false) String title,

            @Parameter(description = "작성자 닉네임", required = false)
            @RequestParam(required = false) String writer,

            @Parameter(description = "검색 시작일", required = false, example = "2023-01-01")
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,

            @Parameter(description = "검색 종료일", required = false, example = "2023-12-31")
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,

            @Parameter(description = "페이지 번호", example = "1")
            @RequestParam(defaultValue = "1") int page,

            @Parameter(description = "페이지당 게시글 수", example = "10")
            @RequestParam(defaultValue = "10") int recordSize
    ) {
        SearchDTO searchDTO = new SearchDTO();
        searchDTO.setCountry(country);
        searchDTO.setCommunityPostId(number);
        searchDTO.setTitle(title);
        searchDTO.setNickname(writer);
        searchDTO.setStartDate(startDate);
        searchDTO.setEndDate(endDate);
        searchDTO.setPage(page);
        searchDTO.setRecordSize(recordSize);

        return communityService.searchCommunityPosts(searchDTO);
    }
}
