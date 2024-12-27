package com.project.Journey.login.member.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MemberDTO {

    @Schema(description = "회원 아이디 (고유값)", example = "user123", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "ID는 필수 값입니다")
    private String id;

    @Schema(description = "사용자 이름(닉네임)", example = "홍길동", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "이름은 필수 값입니다")
    private String name;

    @Schema(description = "비밀번호", example = "pass1234", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "비밀번호는 필수 값입니다")
    private String password;

    @Schema(description = "이메일", example = "test@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @Email(message = "올바른 이메일 형식이어야 합니다")
    @NotBlank(message = "이메일은 필수 값입니다")
    private String email;

    @Schema(description = "소셜 로그인 타입 (예: GOOGLE, KAKAO, NAVER 등)", example = "GOOGLE")
    private String socialType;

    @Schema(description = "소셜 로그인 ID", example = "1234567890")
    private String socialId;

}
