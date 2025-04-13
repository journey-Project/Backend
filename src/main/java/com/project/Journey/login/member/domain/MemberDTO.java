package com.project.Journey.login.member.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "회원 가입에 사용되는 DTO")
@Getter
@Setter
public class MemberDTO {

    @Schema(description = "사용자가 로그인할 때 입력하는 로그인 ID (회원가입 시 생성한 ID)", example = "user123")
    @NotBlank
    private String loginId;

    @Schema(description = "사용자 실명", example = "홍길동", nullable = false)
    @NotBlank
    private String name;

    @Schema(description = "사용자 비밀번호 (6자 이상)", example = "password123")
    @NotBlank
    @Size(min = 6)
    private String password;

    @Schema(description = "닉네임 (입력하지 않으면 실명으로 설정됩니다)", example = "홍길동", nullable = true)
    private String nickname;

    @Schema(description = "이메일(중복 불가)", example = "test@example.com", nullable = false)
    @NotBlank
    @Email
    private String email;
}