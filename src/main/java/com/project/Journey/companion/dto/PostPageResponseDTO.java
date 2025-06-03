package com.project.Journey.companion.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostPageResponseDTO {

    private Long postId;
    private String destination; //장소
    private LocalDate startDate; //여행 시작일
    private LocalDate endDate; //여행 종료일
    private int max_participants; //참가자 수
    private String title; //제목
    private String coverImageUrl; //커버이미지
    private String country; //국가
    private LocalDate created_at; // 글 작성일

}
