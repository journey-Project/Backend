package com.project.Journey.community.dto;

import com.project.Journey.community.entity.CommunityImage;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityResponseDTO {

    private Long communityPostId;
    private String loginId;
    private String nickname;
    private String country;
    private String title;
    private String content;
    private int viewCount;
    private int commentCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String profileImageUrl;
    private List<String> imageUrls;

}
