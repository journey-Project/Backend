package com.project.Journey.login.member.domain;

import lombok.Data;

@Data
public class MemberDTO {

    @NotBlank
    private String id;

    @NotBlank
    private String name;
    @NotBlank
    private String password;
    @NotBlank
    @Email
    private String email;

    // Oauth2 최초 로그인 후 회원가입 시 필요한 정보
    private String socialType;
    private String socialId;

}
