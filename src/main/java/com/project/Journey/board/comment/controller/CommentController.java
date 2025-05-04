package com.project.Journey.board.comment.controller;

import com.project.Journey.board.comment.entity.Comment;
import com.project.Journey.board.comment.entity.CommentResponseDTO;
import com.project.Journey.board.comment.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "댓글", description = "댓글 및 대댓글(1단계) API")
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @Getter @Setter
    static class CommentRequest {
        @Schema(description = "작성자 Member PK", example = "1")
        private Long memberId;

        @Schema(description = "댓글 또는 대댓글 본문", example = "안녕하세요!")
        private String content;

        @Schema(description = "부모 댓글 ID(대댓글일 때만)", nullable = true)
        private Long parentCommentId;
    }

    @Operation(summary = "댓글/대댓글 작성")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping("/{postId}/comments")
    public ResponseEntity<CommentResponseDTO> createComment(
            @PathVariable Long postId,
            @RequestBody CommentRequest req) {

        Comment saved = commentService.createComment(
                postId, req.getMemberId(), req.getContent(), req.getParentCommentId());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CommentResponseDTO.from(saved));
    }

    @Operation(summary = "게시글 댓글 목록 조회")
    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<CommentResponseDTO>> getComments(@PathVariable Long postId) {

        List<CommentResponseDTO> list = commentService.getCommentsByPost(postId)
                .stream()
                .map(CommentResponseDTO::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(list);
    }

    @Operation(summary = "대댓글 목록 조회")
    @GetMapping("/comments/{commentId}/replies")
    public ResponseEntity<List<CommentResponseDTO>> getReplies(@PathVariable Long commentId) {

        List<CommentResponseDTO> list = commentService.getReplies(commentId)
                .stream()
                .map(CommentResponseDTO::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(list);
    }

    @Operation(summary = "댓글/대댓글 수정")
    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<CommentResponseDTO> updateComment(
            @PathVariable Long commentId,
            @RequestBody CommentRequest req) {

        Comment updated = commentService.updateComment(commentId, req.getContent());
        return ResponseEntity.ok(CommentResponseDTO.from(updated));
    }

    @Operation(summary = "댓글/대댓글 삭제")
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {

        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();   // 204 No-Content
    }
}
