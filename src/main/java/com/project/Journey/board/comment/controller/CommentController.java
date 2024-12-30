package com.project.Journey.board.comment.controller;

import com.project.Journey.board.comment.domain.CommentDTO;
import com.project.Journey.board.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    // 댓글 생성
    @PostMapping("/create")
    public ResponseEntity<Long> createComment(@RequestBody CommentDTO commentDTO) {
        Long commentId = commentService.createComment(commentDTO);
        return ResponseEntity.ok(commentId);
    }

    // 특정 게시글에 대한 댓글 목록 조회
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentDTO>> getCommentsByPostId(@PathVariable Long postId) {
        List<CommentDTO> commentList = commentService.getCommentsByPostId(postId);
        return ResponseEntity.ok(commentList);
    }

    // 댓글 수정
    @PutMapping("/update/{commentId}")
    public ResponseEntity<Void> updateComment(@PathVariable Long commentId, @RequestBody CommentDTO commentDTO) {
        commentService.updateComment(commentId, commentDTO.getContent());
        return ResponseEntity.noContent().build();
    }

    // 댓글 삭제
    @DeleteMapping("/delete/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}