package com.project.Journey.board.controller;


import com.project.Journey.board.dto.PostDTO;
import com.project.Journey.board.entity.Post;
import com.project.Journey.board.exception.PostException;
import com.project.Journey.board.service.PostService;
import com.project.Journey.login.jwt.constants.JwtConstants;
import com.project.Journey.login.jwt.constants.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.JwtBearerOAuth2AuthorizedClientProvider;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;


    // 게시글 저장
    @PostMapping("api/posts/save")
    public ResponseEntity<Long> createPost(@RequestBody PostDTO postDTO) {
        try{
            Long savedPostId = postService.savePost(postDTO);
            return ResponseEntity.ok(savedPostId);
        } catch (Exception e){
            throw new PostException("게시글 저장 중 오류가 발생했습니다.", HttpStatus.BAD_REQUEST);
        }

    }

    // 모든 게시글 조회
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
    @DeleteMapping("api/posts/delete/{post_id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long post_id) {
        try{
            postService.deletePost(post_id);
            return ResponseEntity.noContent().build();
        } catch (Exception e){ // post_id가 없는 게시물 삭제 시 예외처리 필요
            throw new PostException("게시글 삭제 중 오류가 발생했습니다.", HttpStatus.NOT_FOUND);
        }

    }

    //게시글 수정
    @PutMapping("api/posts/update/{post_id}")
    public ResponseEntity<String> updatePost(@PathVariable Long post_id,@RequestBody PostDTO postDTO){
        try{
            postService.updatePostById(post_id, postDTO);
            return ResponseEntity.ok("게시글이 성공적으로 수정되었습니다.");
        } catch (Exception e){
            throw new PostException("게시글 수정 중 오류가 발생했습니다.", HttpStatus.BAD_REQUEST);
        }

    }

    //조회수가 높은 순서대로 게시물 조회
    @GetMapping("api/posts/top-views")
    public List<Post> getTopViewedPosts(){
        try{
            return postService.getPostsByViewCount();
        }catch (Exception e){
            throw new PostException("핫 게시글 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }


    // post_id로 게시글 조회 + 조회 수 증가시키기
    @GetMapping("api/posts/getIncrementView/{post_id}")
    public PostDTO incrementPostView(@PathVariable Long post_id) {
        try{
            return postService.getPostByIdAndIncrementView(post_id);
        }catch (Exception e){
            throw new PostException("해당 게시글이 존재하지 않습니다.", HttpStatus.NOT_FOUND);
        }

    }

    //user_id로 게시글 조회
    @GetMapping("api/posts/get/user_id/{user_id}")
    public List<PostDTO> getPostsByUser_id(@PathVariable String user_id){
        try{
            List<PostDTO> posts = postService.getPostsByUserId(user_id);
            return posts;
        } catch (Exception e){
            throw new PostException("해당 user_id의 게시글이 존재하지 않습니다.",HttpStatus.NOT_FOUND);
        }
    }


}
