package com.project.Journey.board.comment.controller;

import com.project.Journey.board.comment.domain.CommentDTO;
import com.project.Journey.board.comment.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "댓글 API", description = "댓글 및 대댓글 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    // 댓글(대댓글) 생성
    @Operation(summary = "댓글 생성", description = "새로운 댓글 또는 대댓글을 생성합니다.")
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createComment(@Valid @RequestBody CommentDTO dto) {
        Long commentId = commentService.createComment(dto);
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "댓글이 생성되었습니다.",
                "commentId", commentId
        ));
    }

    // 게시글 전체 댓글(대댓글 포함) 조회
    @Operation(summary = "게시글 전체 댓글 조회", description = "특정 게시글의 모든 댓글 및 대댓글을 조회합니다.")
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentDTO>> getAllComments(@PathVariable Long postId) {
        List<CommentDTO> commentList = commentService.getAllCommentsByPostId(postId);
        return ResponseEntity.ok(commentList);
    }

    // 댓글 수정
    @Operation(summary = "댓글 수정", description = "댓글 내용을 수정합니다.")
    @PutMapping("/update/{commentId}")
    public ResponseEntity<Map<String, String>> updateComment(@PathVariable Long commentId, @Valid @RequestBody CommentDTO dto) {
        commentService.updateComment(commentId, dto.getContent());
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "댓글이 수정되었습니다."
        ));
    }

    // 댓글 삭제
    @Operation(summary = "댓글 삭제", description = "특정 댓글을 삭제합니다.")
    @DeleteMapping("/delete/{commentId}")
    public ResponseEntity<Map<String, String>> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "댓글이 삭제되었습니다."
        ));
    }
}