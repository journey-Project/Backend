package com.project.Journey.story.dto;

import com.project.Journey.story.entity.Story;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class StoryResponseDTO {

    private String imageUrl;
    private Long authorId;
    private String authorUsername;
    private String authorProfileImageUrl;
    private LocalDateTime createdAt;
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
