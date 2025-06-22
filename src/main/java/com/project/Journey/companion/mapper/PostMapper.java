package com.project.Journey.companion.mapper;

import com.project.Journey.companion.dto.PostResponseDTO;
import com.project.Journey.companion.entity.Post;
import com.project.Journey.companion.entity.PostImage;

import java.util.List;
import java.util.stream.Collectors;

public class PostMapper {

    public static PostResponseDTO toResponseDTO(Post post, List<PostImage> images) {
        return PostResponseDTO.builder()
                .postId(post.getPostId())
                .writerId(post.getMember().getId())
                .isMine(false) // 로그인 사용자 정보 필요시 서비스에서 설정
                .nickname(post.getMember().getNickname())
                .title(post.getTitle())
                .content(post.getContent())
                .destination(post.getDestination())
                .start_date(post.getStartDate())
                .end_date(post.getEndDate())
                .max_participants(post.getMax_participants())
                .view_count(post.getView_count())
                .comment_count(post.getComment_count())
                .created_at(post.getCreatedAt())
                .updated_at(post.getUpdated_at())
                .coverImageUrl(post.getCoverImageUrl())
                .profileImageUrl(post.getMember().getProfileImage())
                .country(post.getCountry())
                .imageUrls(images.stream()
                        .map(PostImage::getPostImageUrl)
                        .collect(Collectors.toList()))
                .build();
    }
}