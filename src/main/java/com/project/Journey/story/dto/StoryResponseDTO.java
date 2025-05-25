package com.project.Journey.story.dto;

import com.project.Journey.story.entity.Story;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class StoryResponseDTO {

    @Schema(description = "스토리 이미지 URL", example = "https://example.com/story.jpg")
    private String imageUrl;

    @Schema(description = "작성자 ID", example = "1")
    private Long authorId;

    @Schema(description = "작성자 로그인 ID", example = "user1")
    private String authorUsername;

    @Schema(description = "작성자 프로필 이미지 URL", example = "https://example.com/profile.jpg")
    private String authorProfileImageUrl;

    @Schema(description = "스토리 생성 시간", example = "2025-05-25T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "스토리 만료 시간", example = "2025-05-26T10:30:00")
    private LocalDateTime expireAt;

    public StoryResponseDTO(Story story){
        this.imageUrl= story.getImageUrl();
        this.authorId= story.getAuthor().getId();
        this.authorUsername = story.getAuthor().getLoginId();
        this.authorProfileImageUrl=story.getAuthor().getProfileImage();
        this.createdAt=story.getCreatedAt();
        this.expireAt=story.getExpireAt();
    }
}
