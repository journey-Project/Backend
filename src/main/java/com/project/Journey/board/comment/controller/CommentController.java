package com.project.Journey.board.comment.controller;

import com.project.Journey.board.comment.domain.CommentDTO;
import com.project.Journey.board.comment.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "댓글 API", description = "댓글 및 대댓글 관리 API- 현재 수정중입니다")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createComment(@Valid @RequestBody CommentDTO dto) {
        Long commentId = commentService.createComment(dto);
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "댓글이 생성되었습니다.",
                "commentId", commentId
        ));
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentDTO>> getAllComments(@PathVariable Long postId) {
        List<CommentDTO> commentList = commentService.getAllCommentsByPostId(postId);
        return ResponseEntity.ok(commentList);
    }

    @PutMapping("/update/{commentId}")
    public ResponseEntity<Map<String, String>> updateComment(@PathVariable Long commentId, @Valid @RequestBody CommentDTO dto) {
        commentService.updateComment(commentId, dto.getContent());
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "댓글이 수정되었습니다."
        ));
    }

    @DeleteMapping("/delete/{commentId}")
    public ResponseEntity<Map<String, String>> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "댓글이 삭제되었습니다."
        ));
    }
}