package com.project.Journey.community.dto;

import com.project.Journey.community.paging.Pagination;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchDTO {
    private int page = 1;                 // 기본 페이지 번호
    private int recordSize = 10;          // 페이지당 출력할 데이터 개수
    private int pageSize = 10;            // 하단 페이지 버튼 개수
    private String keyword;               // 검색 키워드 (제목 or 작성자)
    private LocalDate startDate;          // 검색 시작 날짜
    private LocalDate endDate;            // 검색 종료 날짜
    private Pagination pagination;        // 페이지네이션 정보
}