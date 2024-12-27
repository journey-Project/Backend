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
    //@JoinColumn(name = "user_id", nullable = false)
    @Column(name = "user_id")
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


    public void updateTitle(String title){
        this.title=title;
    }

    public void updateContent(String content){
        this.content=content;
    }

    public void updateDestination(String destination){
        this.destination=destination;
    }


    public void updateStartDate(LocalDate start_date){
        this.start_date=start_date;
    }

    public void updateEndDate(LocalDate end_date){
        this.end_date=end_date;
    }

    public void updateMaxParticipants(int max_participants){
        this.max_participants=max_participants;
    }

    //글을 업데이트한 시각
    public void updateUpdateTime(LocalDateTime localDateTime){
        this.updated_at = LocalDateTime.now();
    }

}
