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
                .commentCount(community.getComment_count())
                .createdAt(community.getCreatedAt())
                .build();
    }
}

