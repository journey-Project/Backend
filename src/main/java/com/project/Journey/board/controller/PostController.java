package com.project.Journey.board.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.project.Journey.board.dto.*;
import com.project.Journey.board.entity.Post;
import com.project.Journey.board.exception.PostException;
import com.project.Journey.board.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.JwtBearerOAuth2AuthorizedClientProvider;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "동행자 모집 게시글 관리", description = "동행자모집 게시판 관련 API")
public class PostController {

    @Autowired
    private final PostService postService;


    // 게시글 저장
    @Operation(summary = "동행자 모집 게시글 저장", description = """
            새로운 동행자 모집 게시글을 저장합니다.
            
            -post 필드 : userId, country, title, content, destination, startDate, endDate,
            participants 필수
            
            post : {
            "userId": "미국여행자3", -> String
            "country": "미국", -> String
            "title": "미국 뉴욕으로~~~", -> String
            "startDate": "2025-03-23", -> LocalDateTime
            "endDate": "2024-03-23", -> LocalDateTime
            "content": "미국 뉴욕여행같이해요~~!!!", ->String
            "participants" : 5, -> int
            "destination" : "미국" -> String
            } 
           
            coverImage : (MultipartFile) 이미지파일.jpg 
            images : ["이미지파일1.jpg", "이미지파일2.jpg", ...]
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글이 성공적으로 저장되었습니다."),
            @ApiResponse(responseCode = "400", description = "입력값이 잘못되었습니다.")
    })
    @PostMapping("api/posts/save")
    public ResponseEntity<Long> createPost(
            @Parameter(description = "게시글 정보(JSON 형식)", required = true)
            @RequestPart("post") String postJson,

            @Parameter(description = "커버 이미지 파일", required = false)
            @RequestPart(value = "coverImage", required = false) MultipartFile coverImage,

            @Parameter(description = "게시글에 첨부할 이미지 파일 리스트", required = false)
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {

        try{
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // 날짜를 타임스탬프가 아니라 문자열로
            PostRequestDTO postRequestDTO = objectMapper.readValue(postJson, PostRequestDTO.class);

            Long postId = postService.savePost(postRequestDTO, coverImage, images);
            return ResponseEntity.ok(postId);
        }catch (Exception e){
            e.printStackTrace();
            throw new PostException("동행자 모집 게시글 저장 중 오류가 발생했습니다.", HttpStatus.BAD_REQUEST);
        }
    }


    // 모든 게시글 조회
    @Operation(summary = "모든 게시글 조회", description = """
            모든 게시글을 조회합니다.
            API 요청 예시 : http://localhost:8080/api/posts/getAll
            
            response :
            [
                {
                    "postId": null,
                        "user_id": "user1",
                        "title": "유럽 여행 같이 가실 분 구합니다!",
                        "content": "유럽 여행 같이가요~~!",
                        "destination": "프랑스",
                        "start_date": "2025-01-24",
                        "end_date": "2024-01-26",
                        "max_participants": 7,
                        "view_count": 2,
                        "comment_count": 6,
                        "created_at": "2025-01-24T07:05:10.209152",
                        "updated_at": "2025-01-24T07:07:54.341736",
                        "coverImageUrl": "커버이미지주소.jpg",
                        "profileImageUrl": "프로필이미지주소.jpg",
                        "country": "국내",
                        "imageUrls": ["이미지주소1.jpg", "이미지주소2.jpg"...]
                } ....
            ]
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 목록이 성공적으로 반환되었습니다."),
            @ApiResponse(responseCode = "500", description = "서버에서 오류가 발생했습니다.")
    })
    @GetMapping("api/posts/getAll")
    public List<PostDTO> getAllPosts(HttpServletRequest request) {
        try {
            return postService.getAllPosts();
        } catch (Exception e){
            throw new PostException("게시글 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    // 게시글 삭제
    @Operation(summary = "게시글 삭제", description = """
        특정 게시글을 삭제합니다.
        response 예시
        정상 삭제되었을 경우 : 1    
    """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "게시글이 성공적으로 삭제되었습니다."),
            @ApiResponse(responseCode = "404", description = "해당 게시글을 찾을 수 없습니다.")
    })
    @DeleteMapping("api/posts/delete/{post_id}")
    public ResponseEntity<Void> deletePost(
            @Parameter(description = "삭제할 게시글의 post_id", required = true, example = "1") @PathVariable Long post_id) {
        try{
            postService.deletePost(post_id);
            return ResponseEntity.noContent().build();
        } catch (Exception e){ // post_id가 없는 게시물 삭제 시 예외처리 필요
            throw new PostException("게시글 삭제 중 오류가 발생했습니다.", HttpStatus.NOT_FOUND);
        }

    }

    //게시글 수정
    @Operation(summary = "게시글 수정", description = """
            특정 게시글을 수정합니다.
            -> title, content, destination, start_date, end_date,
            max_participants, country, coverImageUrl, imagesUrl 필드 중 수정하고 싶은 필드를 
            수정하고 업데이트 
            
            
            1. /api/posts/getIncrementView/{postId}로 업데이트 할 게시글을 불러옴
            
            2. /api/posts/update/{post_id}로 json에서 coverImage를 삭제하고 싶으면 빈 문자열("")로, 첨부파일이미지를 삭제하고 싶으면 해당 이미지 주소를 지우면 됩니다!(남기고 싶은 이미지는 지우지 않으면 유지됨)
               만약 새로 커버 이미지를 업데이트 하고 싶으면 newCoverImage를 key값으로 두고 value를 MultipartFile 객체로,
               첨부파일 이미지를 추가하고 싶으면 newImages를 key값으로 두고 value를 List<MultipartFile>로 지정해주고\s
               API를 호출하면 됩니다!
               
               
            key : post, value: json( /api/posts/getIncrementView/{postId}의 response객체)
             {
                "postId": 8,
                "user_id": "여행자",
                "title": "캘리포니아 같이 가실 분 구해요!!! - 게시글 수정", -> 수정 가능
                "content": "이번 방학때 캘리포니아에 가게 됐는데 혼자 여행가게 되었습니다!\\n같이 동행하실 분 구해요", -> 수정 가능
                "destination": "미국 캘리포니아", -> 수정 가능
                "start_date": "2025-03-23", -> 수정 가능
                "end_date": "2025-03-27", -> 수정 가능
                "max_participants": 7, -> 수정 가능
                "view_count": 11,
                "comment_count": 0,
                "created_at": "2025-04-30T19:55:54.950551",
                "updated_at": "2025-05-02T17:18:46.888407",
                "coverImageUrl": "커버이미지url.jpg", -> 수정 가능
                "profileImageUrl": "", 
                "country": "국내", -> 수정 가능
                "imageUrls": ["첨부이미지url.jpg"] -> 수정 가능
            }
                       
            추가하고 싶은 이미지가 있다면           
            key : newImages , value : List<MultipartFile>\s
            key : newCoverImage, value : MultipartFile 
            추가       
                    """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글이 성공적으로 수정되었습니다."),
            @ApiResponse(responseCode = "400", description = "수정 요청값이 잘못되었습니다."),
            @ApiResponse(responseCode = "404", description = "해당 게시글을 찾을 수 없습니다.")
    })

    @PutMapping(value = "/api/posts/update/{post_id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updatePost(
            @Parameter(description = "수정할 게시글의 post_id", required = true)
            @PathVariable Long post_id,

            @Parameter(description = "업데이트 할 게시글 정보(JSON 형식)", required = true)
            @RequestPart("post") String postJson,

            @Parameter(description = "추가할 첨부파일 이미지(List<MultipartFile>)")
            @RequestPart(value = "newImages", required = false) List<MultipartFile> newImages,

            @Parameter(description = "변경할 커버 이미지 (MultipartFile)")
            @RequestPart(value = "newCoverImage", required = false) MultipartFile newCoverImage
    ) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());

            PostDTO postDTO = mapper.readValue(postJson, PostDTO.class);

            postService.updatePostById(post_id, postDTO, newImages, newCoverImage);
            return ResponseEntity.ok("게시글이 성공적으로 수정되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            throw new PostException("게시글 수정 중 오류가 발생했습니다.", HttpStatus.BAD_REQUEST);
        }
    }

    //조회수가 높은 순서대로 게시물 조회

    @Operation(summary = "조회수가 높은 게시글 조회", description = "조회수가 높은 순서대로 게시글을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회수가 높은 게시글 목록이 성공적으로 반환되었습니다."),
            @ApiResponse(responseCode = "500", description = "서버에서 오류가 발생했습니다.")
    })
    @GetMapping("api/posts/top-views")
    public List<Post> getTopViewedPosts(){
        try{
            return postService.getPostsByViewCount();
        }catch (Exception e){
            throw new PostException("핫 게시글 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }


    // post_id로 게시글 조회 + 조회 수 증가시키기
    @Operation(summary = "게시글 조회 및 조회수 증가", description = """
            특정 게시글을 조회하고 조회수를 증가시킵니다.
            
            API 요청 예시 : http://localhost:8080/api/posts/getIncrementView/13
            
            response :
            {
               "postId": 21,
                   "user_id": "미국여행자2",
                   "title": "미국으로 떠나요2",
                   "content": "미국 뉴욕여행같이해요~~!!!",
                   "destination": "미국",
                   "start_date": "2025-03-23",
                   "end_date": "2024-03-23",
                   "max_participants": 6,
                   "view_count": 3,
                   "comment_count": 0,
                   "created_at": "2025-03-23T19:19:59.533322",
                   "updated_at": "2025-03-23T19:19:59.533322",
                   "coverImageUrl": "https://journeybucket0.s3.ap-northeast-2.amazonaws.com/APPLICATION/~~~.jpg",
                   "profileImageUrl": "",
                   "country": "미국",
                   "imageUrls": [
                       "https://journeybucket0.s3.ap-northeast-2.amazonaws.com/APPLICATION/~~~.jpg",
                       "https://journeybucket0.s3.ap-northeast-2.amazonaws.com/APPLICATION/~~~.jpg"
                   ]
            }
            
            DTO를 반환하고 "view_count"가 1 증가
            """
            )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글이 성공적으로 조회되고 조회수가 증가되었습니다."),
            @ApiResponse(responseCode = "404", description = "해당 게시글을 찾을 수 없습니다.")
    })
    @GetMapping("api/posts/getIncrementView/{post_id}")
    public PostDTO incrementPostView(@Parameter(description = "조회할 게시글의 ID", required = true, example = "1") @PathVariable Long post_id) {
        try{
            return postService.getPostByIdAndIncrementView(post_id);
        }catch (Exception e){
            throw new PostException("해당 게시글이 존재하지 않습니다.", HttpStatus.NOT_FOUND);
        }

    }

    //user_id로 게시글 조회
    @Operation(summary = "사용자 게시글 조회", description = """
            특정 사용자의 user_id로 게시글을 조회합니다.
            
            API 요청 예시 : http://localhost:8080/api/posts/get/user_id/user1234
            
            response : [
            {
                    "user_id": "user1234",
                    "title": "제주도 여행",
                    "content": "제주도로 떠나요!",
                    "destination": "한국",
                    "start_date": "2024-12-30",
                    "end_date": "2024-12-31",
                    "max_participants": 7,
                    "view_count": 0,
                    "comment_count": 0,
                    "created_at": "2025-02-17T22:23:14.304542",
                    "updated_at": "2025-02-17T22:23:14.305558",
                    "imageUrl": "https://s3.amazonaws.com/bucket-name/path/to/image2.jpg",
                    "country": "한국"
                },
                .....
            ]
            
            """
            )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "해당 사용자의 게시글 목록이 성공적으로 반환되었습니다."),
            @ApiResponse(responseCode = "404", description = "해당 사용자의 게시글을 찾을 수 없습니다.")
    })
    @GetMapping("api/posts/get/user_id/{user_id}")
    public List<PostDTO> getPostsByUser_id( @Parameter(description = "게시글을 조회할 사용자의 user_id", required = true, example = "user123") @PathVariable String user_id){
        try{
            List<PostDTO> posts = postService.getPostsByUserId(user_id);
            return posts;
        } catch (Exception e){
            throw new PostException("해당 user_id의 게시글이 존재하지 않습니다.",HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping("api/posts/getPostByPage")
    public List<PostDTO> getPostByPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        return postService.getPosts(page,size);
    }


    //국가별 게시글 반환 :
    //예시 : http://localhost:8080/api/posts/getPostByCountry/한국?page=0&size=5
   /*
    @Operation(summary = "국가별 게시글 조회", description = """
            페이지네이션을 적용하여 page와 size를 지정하여 국가별 커뮤니티 게시글을 반환합니다.

            API 요청 예시 : http://localhost:8080/api/posts/getPostByCountry/한국?page=0&size=5

            response :
            [
             {
                    "user_id": "user1234",
                    "title": "제주도 여행",
                    "content": "제주도로 떠나요!",
                    "destination": "한국",
                    "start_date": "2024-12-30",
                    "end_date": "2024-12-31",
                    "max_participants": 7,
                    "view_count": 0,
                    "comment_count": 0,
                    "created_at": "2025-02-17T22:23:14.304542",
                    "updated_at": "2025-02-17T22:23:14.305558",
                    "imageUrl": "https://s3.amazonaws.com/bucket-name/path/to/image2.jpg",
                    "country": "한국"
                },
                {
                    "user_id": "user1234",
                    "title": "부산 여행",
                    "content": "부산으로 떠나요!",
                    "destination": "한국",
                    "start_date": "2024-12-30",
                    "end_date": "2024-12-31",
                    "max_participants": 7,
                    "view_count": 0,
                    "comment_count": 0,
                    "created_at": "2025-02-17T23:19:03.442716",
                    "updated_at": "2025-02-17T23:19:03.442716",
                    "imageUrl": "https://s3.amazonaws.com/bucket-name/path/to/image2.jpg",
                    "country": "한국"
                },....
            ]


            """)*/
    @GetMapping("api/posts/getPostByCountry/{country}")
    public List<PostDTO> getPostByCountry(
            @PathVariable String country,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
            ){
        return postService.getPostsByCountry(country,page,size);
    }


    //동행자 모집 게시글 페이지네이션
    //http://localhost:8080/api/posts/searchPost?title=미국&page=1
    @GetMapping("api/posts/search")
    public ResponseEntity<PostSearchResponseDTO> searchPostsByDateRangeAndCountry(
            @RequestParam("startDate") String startDateStr,
            @RequestParam("endDate") String endDateStr,
            @RequestParam("country") String country,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        LocalDate startDate = LocalDate.parse(startDateStr);
        LocalDate endDate = LocalDate.parse(endDateStr);

        // `page`가 1부터 시작하도록 변환 (Spring Data JPA는 0부터 시작)
        Pageable pageable = PageRequest.of(page - 1, size);

        PostSearchResponseDTO response = postService.getPostsByDateRangeAndCountry(startDate, endDate, country, pageable);
        return ResponseEntity.ok(response);
    }


    //랜덤 동행자 게시글 가져오기
    @Operation(summary = "랜덤 동행자 게시글 조회", description = """
           랜덤으로 동행자 모집 게시글을 count 개수 만큼 반환합니다.
           
           /api/posts/random?count=3 처럼 count 값을 지정해주면 
           count 개수 만큼 게시글이 반환됨
           
            response예시 = [
            {
            "postId": 2,
            "destination": "아이슬란드",
            "startDate": "2025-10-27",
            "endDate": "2025-11-02",
            "max_participants": 4,
            "title": "함께 아이슬란드에서 좋은 추억쌓고 오실 분! 여성분들과 함께 하고 싶습니다",
            "coverImageUrl": "image.jpg",
            "country": "국내" -> 커뮤니티명
            },
            {
            "postId": 15,
            "destination": "한국",
            "startDate": "2024-12-30",
            "endDate": "2024-12-31",
            "max_participants": 7,
            "title": "독도 여행",
            "coverImageUrl": null,
            "country": "국내"
            },
            {
            "postId": 1,
            "destination": "내슈빌",
            "startDate": "2025-01-07",
            "endDate": "2025-01-08",
            "max_participants": 5,
            "title": "내슈빌로 가요",
            "coverImageUrl": null,
            "country": "국내"
            }
            ]
           
            """
    )
    @GetMapping("api/posts/random")
    public ResponseEntity<List<PostPageResponseDTO>> getRandomPosts(
            @Parameter(description = "랜덤으로 가져올 동행자 모집 게시글 개수")
            @RequestParam(value = "count", defaultValue = "3") int count) {

        List<PostPageResponseDTO> randomPosts = postService.getRandomPosts(count);
        return ResponseEntity.ok(randomPosts);
    }

    //동행자 모집 검색 & 페이지네이션



    @Operation(summary = "동행자 모집 게시글 페이지네이션", description = """
            
            예시 : 
            기본 호출
            http://localhost:8080/api/posts/searchPost
            
            country, page, size 지정
            http://localhost:8080/api/posts/searchPost?country=국내&page=1&size=6
           
           
           제목으로 검색 : (제목에 '여행'이라는  단어가 있는 게시글 검색)
           http://localhost:8080/api/posts/searchPost?title=여행
            
           작성자로 검색 : (작성자가 '산책자'인 게시글 검색) 
           http://localhost:8080/api/posts/searchPost?user_id=산책자
           
           번호(postId)로 검색 : (postId가 9번인 게시글 검색) 
           http://localhost:8080/api/posts/searchPost?postId=9
           
           여행시작일, 종료일로 검색..... (2025-04-01부터 2025-05-01 기간 내의 게시글 검색
           -> http://localhost:8080/api/posts/searchPost?startDate=2025-04-01&endDate=2025-05-01
            """
    )
    @GetMapping("/api/posts/searchPost")
    public PostSearchResponse searchPosts(
            @Parameter(description = "검색 파라미터 : country, title, user_id, startDate, endDate, postId ")
            @ModelAttribute PostSearchRequest request,
            @Parameter(description = "검색할 페이지 번호")
            @RequestParam(defaultValue = "1") int page,

            @Parameter(description = "페이지 당 가져올 게시글 수")
            @RequestParam(defaultValue = "6") int size
    ) {
        request.setPage(page);           // 필수: request 내부에 페이지 정보 세팅
        request.setRecordSize(size);    // 필수: 한 페이지 크기 세팅
        return postService.searchPosts(request);
    }

}
