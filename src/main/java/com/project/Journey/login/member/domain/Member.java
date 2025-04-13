package com.project.Journey.login.member.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Getter  // or @Getter @Setter(AccessLevel.PROTECTED) if you want
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String loginId;
    private String name;
    private String nickname;
    private String password;
    private String email;

    @Enumerated(EnumType.STRING)
    private MemberRole role;

    @Enumerated(EnumType.STRING)
    private SocialType socialType;
    private String socialId;
    private String profileImage;

    // 빌더 또는 all-args 생성자
    @Builder
    private Member(String loginId, String name, String nickname,
                   String password, String email,
                   MemberRole role,
                   SocialType socialType,
                   String socialId,
                   String profileImage) {
        this.loginId = loginId;
        this.name = name;
        this.nickname = nickname;
        this.password = password;
        this.email = email;
        this.role = role;
        this.socialType = socialType;
        this.socialId = socialId;
        this.profileImage = profileImage;
    }

    // 정적 팩토리 메서드
    public static Member createNormalUser(MemberDTO dto, PasswordEncoder encoder) {
        return Member.builder()
                .loginId(dto.getLoginId())
                .name(dto.getName())
                .nickname(dto.getName()) // 닉네임은 name으로 자동 설정
                .password(encoder.encode(dto.getPassword()))
                .email(dto.getEmail())
                .role(MemberRole.USER)
                .build();
    }

    public static Member createSocialUser(SocialMemberDTO dto) {
        return Member.builder()
                .loginId(dto.getLoginId())
                .name(dto.getName())
                .nickname(dto.getName())
                .password("SOCIAL_USER")
                .email(dto.getEmail())
                .role(MemberRole.USER)
                .socialType(parseSocialType(dto.getSocialType()))
                .socialId(dto.getSocialId())
                .profileImage(dto.getProfileImage())
                .build();
    }

    private static SocialType parseSocialType(String socialTypeStr) {
        if (socialTypeStr == null || socialTypeStr.isBlank()) {
            return null;
        }
        return SocialType.valueOf(socialTypeStr.toUpperCase());
    }

    // 닉네임 없으면 loginId 반환
    public String getDisplayName() {
        return (this.nickname == null || this.nickname.isBlank())
                ? this.loginId
                : this.nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

}
