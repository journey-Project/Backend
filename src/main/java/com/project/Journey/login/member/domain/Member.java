package com.project.Journey.login.member.domain;

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

    private String profileImage;

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
        this.profileImage = profileImage;
    }

    // Member가 생성되기 전 DTO로 User를 생성할 때 사용하는 코드
    // 비밀번호 암호화까지 동시에 수행
    public static Member createUser(MemberDTO dto, PasswordEncoder passwordEncoder){
        Member member = Member.builder()
                .id(dto.getId())
                .name(dto.getName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(MemberRole.USER)
                .socialType(parseSocialType(dto.getSocialType()))  // 새 메서드로 분리 or inline
                .socialId(dto.getSocialId())
                .profileImage(dto.getProfileImage())
                .build();
        return member;
    }

    private static SocialType parseSocialType(String socialTypeStr) {
        if (socialTypeStr == null || socialTypeStr.isBlank()) {
            return null;
        }
        // 대소문자 구분 없이 변환
        return SocialType.valueOf(socialTypeStr.toUpperCase());
    }
}
