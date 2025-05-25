package com.project.Journey.story.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class StoryRequestDTO {

    @Schema(description = "업로드할 이미지 파일", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private MultipartFile imageFile;

    @Schema(description = "스토리 만료 시간 (선택사항, 기본 24시간)", example = "2025-05-26T12:00:00")
    private LocalDateTime expireAt;
}
