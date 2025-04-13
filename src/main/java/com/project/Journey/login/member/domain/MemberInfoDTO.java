package com.project.Journey.login.member.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "로그인된 사용자의 정보 응답 DTO")
public class MemberInfoDTO {

    @Schema(description = "로그인 ID", example = "kakao_123456")
    private String loginId;

    @Schema(description = "실명", example = "홍길동")
    private String name;

    @Schema(description = "닉네임", example = "길동이")
    private String nickname;

    @Schema(description = "이메일", example = "hong@kakao.com")
    private String email;

    @Schema(description = "프로필 이미지 URL", example = "https://...")
    private String profileImage;

    @Schema(description = "소셜 플랫폼", example = "KAKAO", nullable = true)
    private String socialType;
}
