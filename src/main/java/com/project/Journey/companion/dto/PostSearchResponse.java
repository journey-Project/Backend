package com.project.Journey.companion.dto;


import com.project.Journey.companion.paging.Pagination;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostSearchResponse {
    private List<PostPageResponseDTO> content;
    private Pagination pagination;
}

