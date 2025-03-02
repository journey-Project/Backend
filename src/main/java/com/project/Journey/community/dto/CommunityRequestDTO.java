package com.project.Journey.community.dto;

import com.project.Journey.community.entity.CommunityImage;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityRequestDTO { //커뮤니티 게시글을 생성/수정할 때 사용하는 DTO

    private String user_id;
    private String country;
    private String title;
    private String content;
    private List<String> imageUrls; // 첨부된 이미지 URL 리스트

}
