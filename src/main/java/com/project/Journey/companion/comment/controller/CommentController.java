package com.project.Journey.companion.comment.controller;

import com.project.Journey.companion.comment.entity.Comment;
import com.project.Journey.companion.comment.dto.CommentResponseDTO;
import com.project.Journey.companion.comment.repository.CommentRepository;
import com.project.Journey.companion.comment.service.CommentService;
import com.project.Journey.login.auth.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "동행자 게시글 댓글", description = "댓글 및 대댓글(1단계) API")
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final CommentRepository commentRepository;

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
            @RequestBody CommentRequest req,
            @AuthenticationPrincipal CustomUserDetails principal) {

        Long writerId = principal.getMember().getId(); // 로그인 사용자
        Comment comment = commentService.createComment(postId, writerId, req.getContent(), req.getParentCommentId());
        CommentResponseDTO dto = CommentResponseDTO.from(comment, true);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @Operation(summary = "게시글 댓글 목록 조회",
            description = """
        **루트 댓글 + 1단계 대댓글** 을 함께 반환합니다.  
        - `writerId` : 댓글 작성자 Member PK  
        - `isMine`   : 로그인 사용자가 작성한 댓글 여부  
        - `replies[]`: 대댓글 배열 (없으면 빈 배열)
        """)
    @ApiResponse(responseCode = "200", description = """
```json
[
  {
    "commentId": 11,
    "postId": 3,
    "writerId": 7,
    "displayName": "모아찌",
    "profileImage": "https://…/p7.png",
    "content": "재밌겠네요!",
    "parentCommentId": null,
    "replyCount": 2,
    "isMine": true,
    "isActive": true,
    "createdAt": "2025-05-02T12:34:56",
    "updatedAt": "2025-05-02T12:34:56",
    "replies": [
      {
        "commentId": 12,
        "writerId": 9,
        "displayName": "여행러버",
        "content": "저도 같이 가요!",
        "parentCommentId": 11,
        "replyCount": 0,
        "isMine": false,
        …
      }
    ]
  }
]
```""", content = @Content)
    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<CommentResponseDTO>> getComments(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails principal) {

        Long currentMemberId = principal != null ? principal.getMember().getId() : null;
        List<CommentResponseDTO> list = commentService.getCommentsByPost(postId, currentMemberId);
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "대댓글(1단) 목록 조회",
            description = """
             parentCommentId 에 달린 모든 대댓글을 반환합니다.  
             - writerId  : 작성자 PK  
             - isMine    : 현재 로그인 사용자가 작성했는지 여부  
             """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "부모 댓글 없음")
    })
    @GetMapping("/comments/{commentId}/replies")
    public List<CommentResponseDTO> getReplies(Long parentCommentId, Long currentMemberId) {
        Comment parent = commentRepository.findById(parentCommentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다: " + parentCommentId));

        return commentRepository
                .findByParentCommentOrderByCreatedAtAsc(parent)
                .stream()
                .map(c -> CommentResponseDTO.of(c, currentMemberId != null && c.getMember().getId().equals(currentMemberId)))
                .toList();
    }

    @Operation(summary = "댓글/대댓글 수정")
    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<CommentResponseDTO> updateComment(
            @PathVariable Long commentId,
            @RequestBody CommentRequest req,
            @AuthenticationPrincipal CustomUserDetails principal) {

        Long currentUserId = principal.getMember().getId();

        Comment updated = commentService.updateComment(commentId, req.getContent(), currentUserId);
        return ResponseEntity.ok(CommentResponseDTO.from(updated, true)); // 내 댓글 수정이므로 true

    }

    @Operation(summary = "댓글/대댓글 삭제")
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {

        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();   // 204 No-Content
    }
}
