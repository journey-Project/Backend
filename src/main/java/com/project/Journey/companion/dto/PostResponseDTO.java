package com.project.Journey.companion.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
public class PostResponseDTO {

    private Long postId; //게시글 id
    private Long writerId;
    private boolean isMine;
    private String nickname; //닉네임
    private String title; //제목
    private String content;//내용
    private String destination; //장소
    private LocalDate start_date; //여행 기간 시작일
    private LocalDate end_date; //여행 기간 종료일
    private int max_participants; //희망인원

    private int view_count; // Default: 0 조회수
    private int comment_count; // Default: 0

    private LocalDateTime created_at; // 글 작성일
    private LocalDateTime updated_at;

    private String coverImageUrl;

    private String profileImageUrl;
    private String country; // 국가별 게시판

    @Builder.Default
    private final List<String> imageUrls = List.of();
}
