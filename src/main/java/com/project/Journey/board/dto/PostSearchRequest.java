package com.project.Journey.board.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PostSearchRequest extends PostSearchDTO{
    private String country;
    private String title;
    private String user_id;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long postId; // 게시글 번호로 검색
}
