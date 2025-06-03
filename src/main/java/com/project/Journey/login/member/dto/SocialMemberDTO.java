package com.project.Journey.login.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "소셜 회원가입용 DTO(provider: kakao/naver)")
@Getter
@Setter
public class SocialMemberDTO {
    @Schema(description = "로그인 ID (자동 생성)", example = "kakao_123456789", nullable = false)
    private String loginId;

    @Schema(description = "사용자 실명", example = "홍길동", nullable = false)
    private String name;

    @Schema(description = "닉네임 (초기값: 실명과 동일)", example = "홍길동", nullable = false)
    private String nickname;

    @Schema(description = "이메일", example = "socialuser@kakao.com", nullable = true)
    private String email;

    @Schema(description = "소셜 플랫폼 종류 (kakao/naver)", example = "kakao", nullable = false)
    private String socialType;

    @Schema(description = "소셜 제공자의 고유 ID", example = "987654321", nullable = false)
    private String socialId;

    @Schema(description = "프로필 이미지 URL", example = "https://jjj.net/profile.jpg", nullable = true)
    private String profileImage;

    public void generateFieldsIfEmpty() {
        if (this.loginId == null || this.loginId.isBlank()) {
            this.loginId = this.socialType + "_" + this.socialId;
        }
        if (this.nickname == null || this.nickname.isBlank()) {
            this.nickname = this.name;  // 닉네임 초기값 = 실명
        }
    }
}
