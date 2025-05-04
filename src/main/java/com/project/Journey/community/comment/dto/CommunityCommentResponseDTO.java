package com.project.Journey.community.comment.dto;

import com.project.Journey.community.comment.entity.CommunityComment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder(toBuilder = true)
public class CommunityCommentResponseDTO {

    private Long commentId;
    private Long communityId;
    private Long memberId;
    private String displayName;
    private String profileImage;
    private String content;
    private Long parentCommentId;
    private int  replyCount;
    private boolean isActive;
    private LocalDateTime createdAt;
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

    public static CommunityCommentResponseDTO withReplies(CommunityComment root,
                                                          List<CommunityComment> replies) {
        return CommunityCommentResponseDTO.from(root).toBuilder()
                .replyCount(replies.size())
                .build();
    }

    /* 유틸: List<CommunityComment> → List<DTO> */
    public static List<CommunityCommentResponseDTO> listOf(List<CommunityComment> list) {
        return list.stream().map(CommunityCommentResponseDTO::from).collect(Collectors.toList());
    }
}

