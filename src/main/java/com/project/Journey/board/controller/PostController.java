package com.project.Journey.board.controller;


import com.project.Journey.board.dto.PostDTO;
import com.project.Journey.board.entity.Post;
import com.project.Journey.board.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 게시글 저장
    @PostMapping("api/posts/save")
    public ResponseEntity<Long> createPost(@RequestBody PostDTO postDTO) {
        Long savedPostId = postService.savePost(postDTO);
        return ResponseEntity.ok(savedPostId);
    }

    // 모든 게시글 조회
    @GetMapping("api/posts/getAll")
    public List<PostDTO> getAllPosts() {
        return postService.getAllPosts();
    }


    // post_id로 게시글 조회
    @GetMapping("api/posts/get/{post_id}")
    public PostDTO getPostById(@PathVariable Long post_id) {
        return postService.getPostById(post_id);
    }


    // 게시글 삭제
    @DeleteMapping("api/posts/delete/{post_id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long post_id) {
        postService.deletePost(post_id);
        return ResponseEntity.noContent().build();
    }

    //게시글 수정
    @PutMapping("api/posts/update/{post_id}")
    public  void updatePost(@PathVariable Long post_id,@RequestBody PostDTO postDTO){
       postService.updatePostById(post_id, postDTO);
    }

    //조회수가 높은 순서대로 게시물 조회
    @GetMapping("api/posts/top-views")
    public List<Post> getTopViewedPosts(){
        return postService.getPostsByViewCount();
    }

}
