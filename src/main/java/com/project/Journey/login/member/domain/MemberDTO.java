package com.project.Journey.login.member.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "회원 가입/수정 등에 사용되는 DTO")
@Getter
@Setter
public class MemberDTO {

    @Schema(description = "로그인 ID(중복 불가)", example = "testUser", nullable = false)
    @NotBlank
    private String loginId;

    @Schema(description = "실명(사용자 구분용)", example = "홍길동", nullable = true)
    private String name;

    @Schema(description = "로그인 비밀번호(6자 이상 필수)", example = "123456", nullable = false)
    @NotBlank
    @Size(min = 6)
    private String password;

    private String nickname;

    @Schema(description = "이메일(중복 불가)", example = "test@example.com", nullable = false)
    @NotBlank
    @Email
    private String email;

    @Schema(description = "소셜 타입(kakao, naver, google 등) - 일반회원은 null", example = "kakao", nullable = true)
    private String socialType;

    @Schema(description = "소셜 계정의 고유 ID - 일반회원은 null", nullable = true)
    private String socialId;

    @Schema(description = "프로필 이미지 URL, 가입시 null", nullable = true)
    private String profileImage;
}
