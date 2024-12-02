package com.project.Journey.login.member.domain;

import com.project.Journey.login.oauth2.utils.Oauth2Utils;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Data
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;
    private String id;
    private String name;
    private String password;
    private String email;
    @Enumerated(EnumType.STRING)
    private MemberRole role;

    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    private String socialId;

    public Member(){

    }

    @Builder
    public Member(Long no, String id, String name, String password, String email, MemberRole role, SocialType socialType, String socialId) {
        this.no = no;
        this.id = id;
        this.name = name;
        this.password = password;
        this.email = email;
        this.role = role;
        this.socialType = socialType;
        this.socialId = socialId;
    }

    // Member가 생성되기 전 DTO로 User를 생성할 때 사용하는 코드
    // 비밀번호 암호화까지 동시에 수행
    public static Member createUser(MemberDTO dto, PasswordEncoder passwordEncoder){
        Member member = Member.builder()
                .id(dto.getId())
                .name(dto.getName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(MemberRole.USER) // 역할지정
                .socialType(Oauth2Utils.getSocialType(dto.getSocialType()))
                .socialId(dto.getSocialId())
                .build();
        return member;
    }
}
