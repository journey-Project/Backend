package com.project.Journey.community.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.Journey.community.entity.Community;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityPageResponseDTO {
    private Long communityPostId;
    private String title;
    private String nickname;
    private int commentCount;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime createdAt;


    public static CommunityPageResponseDTO fromEntity(Community community) {
        return CommunityPageResponseDTO.builder()
                .communityPostId(community.getCommunityPostId())
                .title(community.getTitle())
                .nickname(community.getMember().getNickname())
                .createdAt(community.getCreatedAt())
                .commentCount(
                        community.getComments() != null
                                ? (int) community.getComments().stream().filter(c -> c.getParentComment() == null).count()
                                : 0
                ) // 대댓글 제외하고 루트 댓글만 카운트할 수도 있음
                .build();
    }
}

