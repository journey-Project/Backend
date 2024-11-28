package com.project.Journey.board.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue
    private Long post_id;

    //@ManyToOne
   // @JoinColumn(name = "id", nullable = false)
    private String user_id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false, length = 100)
    private String destination;

    @Column(nullable = false)
    private LocalDate start_date;

    @Column(nullable = false)
    private LocalDate end_date;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 1")
    private int max_participants;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private int view_count;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private int comment_count;

    @Column(nullable = false)
    private LocalDateTime created_at;

    @Column(nullable = false)
    private LocalDateTime updated_at;
}
