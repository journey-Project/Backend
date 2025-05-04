package com.project.Journey.community.comment.controller;


import com.project.Journey.community.comment.dto.CommunityCommentDTO;
import com.project.Journey.community.comment.dto.CommunityCommentRequest;
import com.project.Journey.community.comment.dto.CommunityCommentResponseDTO;
import com.project.Journey.community.comment.entity.CommunityComment;
import com.project.Journey.community.comment.service.CommunityCommentService;
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

@Tag(name = "커뮤니티 댓글", description = "커뮤니티 게시판 댓글 · 대댓글(1단) API")
@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class CommunityCommentController {

    private final CommunityCommentService commentService;

    @Operation(summary = "댓글/대댓글 작성",
            description = "parentCommentId 가 null 이면 최상위 댓글, 값이 있으면 해당 댓글의 대댓글로 저장")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청(게시글/부모 댓글 없음 등)")
    })
    @PostMapping("/{communityId}/comments")
    public ResponseEntity<CommunityCommentResponseDTO> create(
            @PathVariable Long communityId,
            @RequestBody  CommunityCommentRequest req) {

        CommunityCommentResponseDTO dto = commentService.createComment(communityId, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }


    @Operation(summary = "댓글 목록 조회",
            description = "특정 커뮤니티 게시글의 최상위 댓글 + 대댓글(1단)을 함께 반환")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "게시글 없음")
    })
    @GetMapping("/{communityId}/comments")
    public ResponseEntity<List<CommunityCommentResponseDTO>> getComments(
            @PathVariable Long communityId) {

        return ResponseEntity.ok(commentService.getRootComments(communityId));
    }

    @Getter @Setter
    static class UpdateRequest {
        @Schema(description = "수정할 내용", example = "댓글을 수정합니다")
        private String content;
    }

    @Operation(summary = "댓글 수정", description = "commentId 로 특정 댓글 내용을 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "댓글 없음")
    })
    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<CommunityCommentResponseDTO> update(
            @PathVariable Long commentId,
            @RequestBody  UpdateRequest req) {

        CommunityCommentResponseDTO dto = commentService.updateComment(commentId, req.getContent());
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "댓글 삭제", description = "commentId 로 댓글을 논리 삭제(isActive=false)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "400", description = "댓글 없음")
    })
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<String> delete(@PathVariable Long commentId) {

        commentService.deleteComment(commentId);
        return ResponseEntity.ok("삭제 완료");
    }
}