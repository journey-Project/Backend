package com.project.Journey.board.comment.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {

    private Long comment_id;
    private String user_id;
    private String content;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    private Long post_id;
}
