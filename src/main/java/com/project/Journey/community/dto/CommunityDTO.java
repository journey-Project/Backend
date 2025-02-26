package com.project.Journey.community.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@Getter
@Setter
public class CommunityDTO {

    private String user_id;
    private String country; //국가별 커뮤니티
    private String title;
    private String content;

    private int view_count; // Default: 0
    private int comment_count; // Default: 0

    private LocalDateTime created_at;
    private LocalDateTime updated_at;

    private String profileImageUrl;
    private String ImageUrl;



}
