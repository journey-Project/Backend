package com.project.Journey.community.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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
    @Column(name = "communityPostId")
    private Long CommunityPostId;

    //커뮤니티 글 작성자
    @Column(name = "user_id")
    private String user_id;

    //커뮤니티 국가
    @Column(name = "country")
    private String country;

    //제목 : 사용자 입력
    @Column(nullable = false, length = 100)
    private String title;

    //내용 : 사용자 입력
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    //커뮤니티 게시글 조회수
    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private int view_count;

    //커뮤니티 게시글 댓글 수
    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private int comment_count;

    //커뮤니티 글 작성일
    @Column(nullable = false)
    private LocalDateTime created_at;

    @Column(nullable = false)
    private LocalDateTime updated_at;

    //프로필 이미지 url
    @Column(nullable = true)
    private String profileImageUrl;

    //첨부파일 이미지 url
    @Column(nullable = true)
    private String ImageUrl;
}
