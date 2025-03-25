package com.project.Journey.board.dto;
import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostSearchResponseDTO {
    private List<PostPageResponseDTO> content; // 현재 페이지의 게시글 목록
    private int currentElements; // 현재 페이지에서 가져온 게시글 개수
    private int page; // 현재 페이지 (1부터 시작)
}