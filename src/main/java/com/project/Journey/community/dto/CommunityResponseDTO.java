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

    private Long CommunityPostId;
    private String nickname;
    private String country;
    private String title;
    private String content;
    private int view_count;
    private int comment_count;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    private String profileImageUrl;
    private List<String> imageUrls;

}
