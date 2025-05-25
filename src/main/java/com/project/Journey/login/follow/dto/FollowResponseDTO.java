package com.project.Journey.login.follow.dto;

import com.project.Journey.login.member.domain.Member;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FollowResponseDTO {

    @Schema(description = "회원의 고유 ID", example = "1")
    private Long memberId;

    @Schema(description = "회원의 로그인 ID", example = "user1")
    private String username;

    @Schema(description = "회원의 프로필 이미지 URL", example = "https://example.com/images/profile1.png")
    private String profileImageUrl;


    public FollowResponseDTO(Member member){
        this.memberId = member.getId();
        this.username = member.getLoginId();
        this.profileImageUrl = member.getProfileImage();
    }
}
