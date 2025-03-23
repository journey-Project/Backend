package com.project.Journey.board.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@Getter
@Setter
public class PostDTO {

    private String user_id;
    private String title;
    private String content;
    private String destination;
    private LocalDate start_date;
    private LocalDate end_date;
    private int max_participants;

    private int view_count; // Default: 0
    private int comment_count; // Default: 0

    private LocalDateTime created_at;
    private LocalDateTime updated_at;

    private String coverImageUrl;

    private String country; // 국가별 게시판

    public PostDTO() {
        // Default constructor
    }
}
