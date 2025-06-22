package com.project.Journey.companion.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PostUpdateRequestDTO {
    private String title;
    private String content;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer max_participants;
    private String destination;
    private String country;
    private List<String> imageUrls;
    private String coverImageUrl;
}
