package com.project.Journey.community.dto;


import com.project.Journey.community.entity.Community;
import com.project.Journey.community.paging.Pagination;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class CommunitySearchResponseDTO {

    private List<CommunityPageResponseDTO> communityList;// 검색된 게시글 목록
    private Pagination pagination;// 페이지네이션 정보
}
