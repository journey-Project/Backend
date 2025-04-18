package com.project.Journey.board.comment.controller;

import com.project.Journey.board.comment.entity.Comment;
import com.project.Journey.board.comment.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "댓글", description = "댓글 및 대댓글(1단계) 관련 API")
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @Getter
    @Setter
    static class CommentRequest {
        @Schema(description = "댓글 작성자 식별자", example = "john123")
        private String userId;

        @Schema(description = "댓글 또는 대댓글 내용", example = "안녕하세요!")
        private String content;

        @Schema(description = "부모 댓글 ID (없으면 최상위, 있으면 대댓글)")
        private Long parentCommentId;
    }

    @Operation(summary = "댓글 생성", description = "게시글에 댓글 또는 대댓글을 생성")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 혹은 존재하지 않는 게시글/부모 댓글")
    })
    @PostMapping("/{postId}/comments")
    public ResponseEntity<?> createComment(
            @PathVariable Long postId,
            @RequestBody CommentRequest request
    ) {
        try {
            Comment comment = commentService.createComment(
                    postId,
                    request.getUserId(),
                    request.getContent(),
                    request.getParentCommentId()
            );
            return ResponseEntity.ok(comment.getCommentId());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "댓글 목록 조회", description = "특정 게시글의 최상위 댓글 목록을 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 혹은 존재하지 않는 게시글")
    })
    @GetMapping("/{postId}/comments")
    public ResponseEntity<?> getComments(@PathVariable Long postId) {
        try {
            List<Comment> comments = commentService.getCommentsByPost(postId);
            return ResponseEntity.ok(comments);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "대댓글 목록 조회", description = "특정 댓글의 대댓글(1단) 목록을 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 혹은 존재하지 않는 댓글")
    })
    @GetMapping("/comments/{commentId}/replies")
    public ResponseEntity<?> getReplies(@PathVariable Long commentId) {
        try {
            List<Comment> replies = commentService.getReplies(commentId);
            return ResponseEntity.ok(replies);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "댓글 수정", description = "특정 댓글 또는 대댓글 내용을 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 혹은 존재하지 않는 댓글")
    })
    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<?> updateComment(
            @PathVariable Long commentId,
            @RequestBody CommentRequest request
    ) {
        try {
            Comment updated = commentService.updateComment(commentId, request.getContent());
            return ResponseEntity.ok(updated.getCommentId());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "댓글 삭제", description = "특정 댓글 또는 대댓글을 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 혹은 존재하지 않는 댓글")
    })
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId) {
        try {
            commentService.deleteComment(commentId);
            return ResponseEntity.ok("삭제 완료");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}