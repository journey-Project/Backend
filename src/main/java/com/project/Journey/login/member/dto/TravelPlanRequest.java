package com.project.Journey.login.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Schema(description = "여행 일정 등록·수정 요청 DTO")
@Getter @Setter
public class TravelPlanRequest {

    @Schema(description = "제목", example = "여름 방콕 여행")
    @NotBlank
    @Size(max = 50)
    private String title;

    @Schema(description = "국가", example = "태국")
    @NotBlank @Size(max = 30)
    private String country;

    @Schema(description = "도시", example = "방콕")
    @NotBlank @Size(max = 30)
    private String city;

    @Schema(description = "출발일", example = "2025-08-01")
    private LocalDate startDate;

    @Schema(description = "도착일", example = "2025-08-10")
    private LocalDate endDate;
}
