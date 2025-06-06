package com.project.Journey.companion.comment.dto;

import com.project.Journey.companion.comment.entity.Comment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Builder
public class CommentResponseDTO {

    private Long commentId;
    private Long postId;
    private Long memberId;
    private Long writerId;
    private boolean isMine;
    private String displayName;
    private String profileImage;
    private String content;
    private Long parentCommentId;
    private int  replyCount;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder.Default
    private List<CommentResponseDTO> replies = List.of();

    public static CommentResponseDTO from(Comment c, boolean mine) {
        return CommentResponseDTO.builder()
                .commentId(c.getCommentId())
                .postId(c.getPost().getPostId())
                .memberId(c.getMember().getId())
                .writerId (c.getMember().getId())
                .isMine(mine)
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

    public static CommentResponseDTO of(Comment c,
                                        boolean mine,
                                        List<CommentResponseDTO> replies) {
        return CommentResponseDTO.builder()
                .commentId(c.getCommentId())
                .postId   (c.getPost().getPostId())
                .writerId (c.getMember().getId())
                .displayName(c.getMember().getDisplayName())
                .profileImage(c.getMember().getProfileImage())
                .content (c.getContent())
                .parentCommentId(
                        c.getParentComment()!=null ? c.getParentComment().getCommentId() : null)
                .replyCount(c.getReplyCount())
                .isMine(mine)
                .isActive(c.isActive())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .replies(replies)
                .build();
    }

    /* 하위 호출용 – replies 없는 경우 */
    public static CommentResponseDTO of(Comment c, boolean mine) {
        return of(c, mine, List.of());
    }
}

