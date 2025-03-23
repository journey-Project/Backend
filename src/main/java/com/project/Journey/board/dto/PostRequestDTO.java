package com.project.Journey.board.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostRequestDTO { //동행자 모집 게시글을 생성/수정할 때 사용하는 DTO


    private String userId; //사용자 아이디
    private String country; //게시판 선택(국가)
    private String title; // 제목
    private LocalDate startDate; //여행기간 시작날짜
    private LocalDate endDate; //여행기간 종료날짜
    private int participants; // 희망 인원수
    private String destination; // 여행 장소
    private MultipartFile coverImageUrl; //커버 이미지
    private String content; //내용
    private List<String> imageUrls;

}
