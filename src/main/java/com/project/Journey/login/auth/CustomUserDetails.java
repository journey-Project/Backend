package com.project.Journey.login.auth;

import com.project.Journey.login.member.domain.Member;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Member 엔티티를 UserDetails로 감싸
 * Spring Security가 이해할 수 있도록 변환
 */
@Getter
public class CustomUserDetails implements UserDetails {

    private final Member member; // 연관된 Member 엔티티
    public Long getId() {
        return member.getId();
    };
    public Member getMember() {
        return member;
    };

    public CustomUserDetails(Member member) {
        this.member = member;
    }

    @Override
    public String getUsername() {
        // Security에서 "username"은 실제로 loginId를 의미
        return member.getLoginId();
    }

    @Override
    public String getPassword() {
        // Member에 저장된 암호화된 비밀번호
        return member.getPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // MemberRole.USER → "ROLE_USER"
        return Collections.singleton(() -> "ROLE_" + member.getRole().name());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정 만료 여부
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 계정 잠금 여부
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 비밀번호 등 자격 증명 만료 여부
    }

    @Override
    public boolean isEnabled() {
        return true; // 계정 활성화 여부
    }

    public void refreshFrom(Member src) {
        this.member.copyProfileFrom(src);
    }


}
