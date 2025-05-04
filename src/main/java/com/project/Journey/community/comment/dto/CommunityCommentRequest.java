package com.project.Journey.community.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "커뮤니티 댓글 작성/수정 요청 DTO")
public class CommunityCommentRequest {

    @Schema(description = "작성자 ID", example = "kaylee77")
    private Long memberId;

    @Schema(description = "내용",     example = "좋은 글이네요!")
    private String content;

    @Schema(description = "부모 댓글 ID (null ➜ 최상위)", example = "15")
    private Long parentCommentId;
}
