package com.project.Journey.login.follow.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FollowRequestDTO {

    @Schema(description = "팔로우할 대상 memberId", example = "2", required = true)
    private Long targetMemberId; //팔로우할 대상 사용자의 memberId
}
