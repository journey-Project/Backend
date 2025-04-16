package com.project.Journey.board.dto;


import com.project.Journey.board.paging.Pagination;
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

