package com.project.Journey.login.member.dto;

import com.project.Journey.login.member.domain.TravelPlan;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Schema(description = "여행 일정 응답 DTO")
@Getter @Builder
public class TravelPlanResponseDTO {
    @Schema(description = "일정 PK", example = "15")
    private Long id;

    @Schema(description = "제목")
    private String title;

    @Schema(description = "국가")
    private String country;

    @Schema(description = "도시")
    private String city;

    @Schema(description = "출발일")
    private LocalDate startDate;

    @Schema(description = "도착일")
    private LocalDate endDate;

    public static TravelPlanResponseDTO of(TravelPlan tp) {
        return TravelPlanResponseDTO.builder()
                .id(tp.getId())
                .title(tp.getTitle())
                .country(tp.getCountry())
                .city(tp.getCity())
                .startDate(tp.getStartDate())
                .endDate(tp.getEndDate())
                .build();
    }
}
