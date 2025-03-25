package com.project.Journey.board.controller;


import com.project.Journey.board.dto.PostDTO;
import com.project.Journey.board.dto.PostRequestDTO;
import com.project.Journey.board.dto.PostSearchResponseDTO;
import com.project.Journey.board.entity.Post;
import com.project.Journey.board.exception.PostException;
import com.project.Journey.board.service.PostService;
import com.project.Journey.login.jwt.constants.JwtConstants;
import com.project.Journey.login.jwt.constants.JwtUtils;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.JwtBearerOAuth2AuthorizedClientProvider;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "게시글 관리", description = "게시판 관련 API")
public class PostController {

    @Autowired
    private final PostService postService;


    // 게시글 저장
    @Operation(summary = "동행자 모집 게시글 저장", description = """
            새로운 동행자 모집 게시글을 저장합니다.
            
            -PostRequestDTO 필드 : userId, title, content, destination, startDate, endDate,
            participants, user_id, country 필수
            
            "post" : {
              "userId": "미국여행자",
              "country": "미국",
              "title": "미국으로 떠나요",
              "startDate": "2025-03-23",
              "endDate": "2024-03-23",
              "content": "미국 뉴욕여행같이해요~~!!!",
              "participants" : 5,
              "destination" : "미국"
            }
            "coverImage": (커버 이미지 파일),
            "images": [(파일1), (파일2), (파일3)]
            
            response : post_id 반환
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글이 성공적으로 저장되었습니다."),
            @ApiResponse(responseCode = "400", description = "입력값이 잘못되었습니다.")
    })
    @PostMapping("api/posts/save")
    public ResponseEntity<Long> createPost(
            @RequestPart("post") PostRequestDTO postRequestDTO,
            @RequestPart(value = "coverImage", required = false) MultipartFile coverImage,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {

        try{
            Long postId = postService.savePost(postRequestDTO, coverImage, images);
            return ResponseEntity.ok(postId);
        }catch (Exception e){
            throw new PostException("동행자 모집 게시글 저장 중 오류가 발생했습니다.", HttpStatus.BAD_REQUEST);
        }
    }


    // 모든 게시글 조회
    @Operation(summary = "모든 게시글 조회", description = """
            API 요청 예시 : http://localhost:8080/api/posts/getAll
            
            response :
            {
                    "user_id": "user1234",
                    "title": "경주 여행",
                    "content": "경주로 떠나요!",
                    "destination": "한국",
                    "start_date": "2024-12-30",
                    "end_date": "2024-12-31",
                    "max_participants": 7,
                    "view_count": 0,
                    "comment_count": 0,
                    "created_at": "2025-02-17T23:22:25.970257",
                    "updated_at": "2025-02-17T23:22:25.970257",
                    "imageUrl": "https://s3.amazonaws.com/bucket-name/path/to/image2.jpg",
                    "country": "한국"
                } ....
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


    /*
    // post_id로 게시글 조회
    @GetMapping("api/posts/get/{post_id}")
    public PostDTO getPostById(@PathVariable Long post_id) {
        try{
            return postService.getPostById(post_id);
        }catch (Exception e){
            throw new PostException("해당 게시글이 존재하지 않습니다.", HttpStatus.NOT_FOUND);
        }

    }
    */

    // 게시글 삭제
    @Operation(summary = "게시글 삭제", description = "특정 게시글을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "게시글이 성공적으로 삭제되었습니다."),
            @ApiResponse(responseCode = "404", description = "해당 게시글을 찾을 수 없습니다.")
    })
    @DeleteMapping("api/posts/delete/{post_id}")
    public ResponseEntity<Void> deletePost( @Parameter(description = "삭제할 게시글의 post_id", required = true, example = "1") @PathVariable Long post_id) {
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
            max_participants, user_id, country, imageUrl 필드 중 수정하고 싶은 필드를 
            수정하고 업데이트 
            
            request 예시
            {
              "title": "이탈리아 여행",
              "content": "이탈리아로 떠나요! -> 글 업데이트~~",
              "destination": "이탈리아",
              "start_date": "2024-12-30",
              "end_date": "2024-12-31",
              "max_participants": 7,
              "user_id" : "user1234",
              "imageUrl": "https://s3.amazonaws.com/bucket-name/path/to/image2.jpg",
              "country" : "이탈리아"
            }
                    
                    
                    """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글이 성공적으로 수정되었습니다."),
            @ApiResponse(responseCode = "400", description = "수정 요청값이 잘못되었습니다."),
            @ApiResponse(responseCode = "404", description = "해당 게시글을 찾을 수 없습니다.")
    })
    @PutMapping("api/posts/update/{post_id}")
    public ResponseEntity<String> updatePost( @Parameter(description = "수정할 게시글의 ID", required = true, example = "1") @PathVariable Long post_id,
                                              @RequestBody @Parameter(description = "게시글 수정에 필요한 정보", required = true) PostDTO postDTO){
        try{
            postService.updatePostById(post_id, postDTO);
            return ResponseEntity.ok("게시글이 성공적으로 수정되었습니다.");
        } catch (Exception e){
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


    @Operation(summary = "사용자 게시글 조회", description = """
            페이지네이션을 적용하여 page와 size를 지정하여 게시글을 반환합니다.
            
            API 요청 예시 : http://localhost:8080/api/posts/getPostByPage?page=0&size=5
            
            -> 첫 페이지의 첫 페이지의 5개 게시글 반환
            [
             {
                    "user_id": "travle",
                    "title": "내슈빌로 가요",
                    "content": "내슈빌 같이 갈 사람!",
                    "destination": "내슈빌",
                    "start_date": "2025-01-07",
                    "end_date": "2025-01-08",
                    "max_participants": 5,
                    "view_count": 7,
                    "comment_count": 1,
                    "created_at": "2025-01-08T10:51:47.845289",
                    "updated_at": "2025-01-08T10:51:47.845289",
                    "imageUrl": null,
                    "country": "미국"
                }
                ....
            ]
            
            
            """)
    @GetMapping("api/posts/getPostByPage")
    public List<PostDTO> getPostByPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        return postService.getPosts(page,size);
    }


    //국가별 게시글 반환 :
    //예시 : http://localhost:8080/api/posts/getPostByCountry/한국?page=0&size=5

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
            
            
            """)
    @GetMapping("api/posts/getPostByCountry/{country}")
    public List<PostDTO> getPostByCountry(
            @PathVariable String country,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
            ){
        return postService.getPostsByCountry(country,page,size);
    }


    //동행자 모집 게시글 페이지네이션
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





}
