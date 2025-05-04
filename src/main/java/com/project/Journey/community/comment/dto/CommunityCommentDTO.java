package com.project.Journey.community.comment.dto;

import com.project.Journey.community.comment.entity.CommunityComment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Schema(description = "커뮤니티 댓글 응답 DTO (대댓글 포함)")
public class CommunityCommentDTO {

    @Schema(description = "댓글 PK")
    private Long commentId;

    @Schema(description = "작성자 ID")
    private String userId;

    @Schema(description = "내용")
    private String content;

    @Schema(description = "작성 시각")
    private LocalDateTime createdAt;

    @Schema(description = "대댓글 목록(1단)", nullable = true)
    private List<CommunityCommentDTO> replies;

    public CommunityCommentDTO(Long id, String userId, String content,
                               LocalDateTime createdAt, List<CommunityCommentDTO> replies) {
        this.commentId  = id;
        this.userId     = userId;
        this.content    = content;
        this.createdAt  = createdAt;
        this.replies    = replies;
    }


}