package com.project.Journey.board.comment.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {

    private Long commentId;
    private String userId;

    @NotBlank
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @NotNull
    private Long postId;

    // 대댓글용
    private Long parentCommentId;       // 부모 댓글 ID (null이면 최상위 댓글로..)
    private int depth;                  // 계층 (0=최상위, 1=대댓글 등)

    // 자식 댓글(대댓글) 목록(필요 시)
    @Builder.Default
    private List<CommentDTO> childComments = new ArrayList<>();

}
