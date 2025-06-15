package com.project.Journey.community.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.Journey.community.comment.entity.CommunityComment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
public class CommunityCommentResponseDTO {

    private Long commentId;
    private Long communityId;
    private Long writerId;     // 댓글 작성자 ID
    private boolean isMine;

    private Long memberId;
    private String displayName;
    private String profileImage;
    private String content;
    private Long parentCommentId;
    private int  replyCount;
    private boolean isActive;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime updatedAt;

    public static CommunityCommentResponseDTO from(CommunityComment c) {
        return CommunityCommentResponseDTO.builder()
                .commentId(c.getCommentId())
                .communityId(c.getCommunity().getCommunityPostId())
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

    @Builder.Default
    private List<CommunityCommentResponseDTO> replies = List.of();

    public static CommunityCommentResponseDTO of(CommunityComment c, boolean isMine,
                                                 List<CommunityCommentResponseDTO> replies) {

        return CommunityCommentResponseDTO.builder()
                .commentId(c.getCommentId())
                .communityId(c.getCommunity().getCommunityPostId())
                .memberId(c.getMember().getId())
                .writerId(c.getMember().getId())
                .isMine(isMine)
                .displayName(c.getMember().getDisplayName())
                .profileImage(c.getMember().getProfileImage())
                .content(c.isActive() ? c.getContent() : "삭제된 댓글입니다.")
                .parentCommentId(c.getParentComment() != null ? c.getParentComment().getCommentId() : null)
                .isActive(c.isActive())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .replies(replies)
                .build();
    }

    public static CommunityCommentResponseDTO of(CommunityComment c, boolean isMine) {
        return of(c, isMine, List.of());
    }

    public static CommunityCommentResponseDTO withReplies(CommunityComment root,
                                                          List<CommunityComment> replies) {
        return CommunityCommentResponseDTO.from(root).toBuilder()
                .replyCount(replies.size())
                .build();
    }

    public static List<CommunityCommentResponseDTO> listOf(List<CommunityComment> list) {
        return list.stream().map(CommunityCommentResponseDTO::from).collect(Collectors.toList());
    }


}

