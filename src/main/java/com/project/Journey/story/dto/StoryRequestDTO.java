package com.project.Journey.story.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class StoryRequestDTO {

    @NotNull
    private MultipartFile imageFile;
    private LocalDateTime expireAt;
}
