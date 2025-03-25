package com.project.Journey.community.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityMainHotPostDTO {

    private Long postId;
    private String user_id;
    private String profileImageUrl;
    private List<String> imageUrls;
    private String content;
    private String country;
}
