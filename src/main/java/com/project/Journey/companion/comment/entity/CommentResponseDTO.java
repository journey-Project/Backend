package com.project.Journey.companion.comment.entity;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter @Builder
public class CommentResponseDTO {

    private Long commentId;
    private Long postId;
    private Long memberId;
    private String displayName;
    private String profileImage;
    private String content;
    private Long parentCommentId;
    private int  replyCount;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CommentResponseDTO from(Comment c) {
        return CommentResponseDTO.builder()
                .commentId(c.getCommentId())
                .postId(c.getPost().getPostId())
                .memberId(c.getMember().getId())
                .displayName(c.getMember().getDisplayName())
                .profileImage(c.getMember().getProfileImage())
                .content(c.getContent())
                .parentCommentId(
                        c.getParentComment() != null ? c.getParentComment().getCommentId() : null)
                .replyCount(c.getReplyCount())
                .isActive(c.isActive())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }
}

