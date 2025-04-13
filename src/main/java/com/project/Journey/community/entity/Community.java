package com.project.Journey.community.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "community")
public class Community {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "community_post_id")
    private Long CommunityPostId;

    //커뮤니티 글 작성자
    @Column(name = "user_id", nullable = false)
    private String user_id;

    //커뮤니티 국가
    @Column(name = "country", nullable = false)
    private String country;

    //제목 : 사용자 입력
    @Column(nullable = false, length = 100)
    private String title;

    //내용 : 사용자 입력
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    //커뮤니티 게시글 조회수
    @Column(name = "view_count", nullable = false, columnDefinition = "INT DEFAULT 0")
    private int viewCount;

    //커뮤니티 게시글 댓글 수
    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private int comment_count;

    //커뮤니티 글 작성일
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    //프로필 이미지 url
    @Column(nullable = true)
    private String profileImageUrl;

    //첨부파일 이미지 (여러 개)
    @OneToMany(mappedBy = "community", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommunityImage> images;

    public void updateCountry(String country){
        this.country=country;
    }

    public void updateTitle(String title){
        this.title=title;
    }

    public void updateContent(String content){
        this.content=content;
    }

    public void updateUpdatedAt(LocalDateTime updatedAt){
        this.updatedAt=updatedAt;
    }

}
