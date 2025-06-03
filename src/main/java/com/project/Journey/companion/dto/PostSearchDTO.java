package com.project.Journey.companion.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostSearchDTO {
    private int page = 1; // 현재 페이지 번호 (1-based)
    private int recordSize = 12; // 한 페이지당 데이터 개수
    private int pageSize = 5; // 페이징에서 보여줄 페이지 수 (ex. 1~5)
}
