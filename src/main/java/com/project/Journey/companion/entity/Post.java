package com.project.Journey.companion.entity;

import com.project.Journey.login.member.domain.Member;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false, length = 100)
    private String destination;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 1")
    private int max_participants;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private int view_count;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private int comment_count;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updated_at;

    //커버 이미지 url
    @Column(nullable = true)
    private String coverImageUrl;

    //국가 컬럼
    //@Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private String country;

    //첨부파일 이미지 (여러 개)
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostImage> images;


    public void updateTitle(String title){
        this.title=title;
    }

    public void updateContent(String content){
        this.content=content;
    }

    public void updateDestination(String destination){
        this.destination=destination;
    }


    public void updateStartDate(LocalDate startDate){
        this.startDate=startDate;
    }

    public void updateEndDate(LocalDate endDate){
        this.endDate=endDate;
    }

    public void updateMaxParticipants(int max_participants){
        this.max_participants=max_participants;
    }

    //글을 업데이트한 시각
    public void updateUpdateTime(LocalDateTime localDateTime){
        this.updated_at = LocalDateTime.now();
    }

    //커버 이미지를 업데이트
    public void updateCoverImageUrl(String coverImageUrl){
        this.coverImageUrl=coverImageUrl;
    }

    //국가이름 업데이트
    public void updateCountry(String country){
        this.country=country;
    }

}
