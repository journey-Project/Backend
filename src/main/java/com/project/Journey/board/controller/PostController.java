package com.project.Journey.board.controller;


import com.project.Journey.board.dto.PostDTO;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.JwtBearerOAuth2AuthorizedClientProvider;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "게시글 관리", description = "게시판 관련 API")
public class PostController {

    private final PostService postService;


    // 게시글 저장
    @Operation(summary = "게시글 저장", description = "새로운 게시글을 저장합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글이 성공적으로 저장되었습니다."),
            @ApiResponse(responseCode = "400", description = "입력값이 잘못되었습니다.")
    })
    @PostMapping("api/posts/save")
    public ResponseEntity<Long> createPost(@RequestBody @Parameter(description = "게시글 저장에 필요한 정보", required = true) PostDTO postDTO) {
        try{
            Long savedPostId = postService.savePost(postDTO);
            return ResponseEntity.ok(savedPostId);
        } catch (Exception e){
            throw new PostException("게시글 저장 중 오류가 발생했습니다.", HttpStatus.BAD_REQUEST);
        }

    }

    // 모든 게시글 조회
    @Operation(summary = "모든 게시글 조회", description = "모든 게시글을 조회합니다.")
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
    @Operation(summary = "게시글 수정", description = "특정 게시글을 수정합니다.")
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
    @Operation(summary = "게시글 조회 및 조회수 증가", description = "특정 게시글을 조회하고 조회수를 증가시킵니다.")
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
    @Operation(summary = "사용자 게시글 조회", description = "특정 사용자의 user_id로 게시글을 조회합니다.")
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

    //페이지네이션 적용
    //http://localhost:8080/posts?page=0&size=5 -> 첫 페이지의 첫 페이지의 5개 게시글 반환
    @GetMapping("api/posts/getPostByPage")
    public List<PostDTO> getPostByPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        return postService.getPosts(page,size);
    }


}
