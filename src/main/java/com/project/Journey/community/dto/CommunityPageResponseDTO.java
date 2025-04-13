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
    private String user_id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime createdAt;

    private String country;

    public static CommunityPageResponseDTO fromEntity(Community community) {
        return CommunityPageResponseDTO.builder()
                .communityPostId(community.getCommunityPostId())
                .title(community.getTitle())
                .user_id(community.getUser_id())
                .createdAt(community.getCreatedAt())
                .country(community.getCountry())
                .build();
    }
}

