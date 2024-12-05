package com.project.Journey.login.member.domain;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MemberDTO {

    @NotBlank(message = "ID는 필수 값입니다")
    private String id;

    @NotBlank(message = "이름은 필수 값입니다")
    private String name;

    @NotBlank(message = "비밀번호는 필수 값입니다")
    private String password;

    @NotBlank(message = "이메일은 필수 값입니다")
    @Email(message = "올바른 이메일 형식이어야 합니다")
    private String email;

    // Oauth2 최초 로그인 후 회원가입 시 필요한 정보
    private String socialType;
    private String socialId;

}
