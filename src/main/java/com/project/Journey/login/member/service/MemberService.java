package com.project.Journey.login.member.service;

import com.project.Journey.login.member.domain.Member;
import com.project.Journey.login.member.domain.MemberDTO;
import com.project.Journey.login.member.domain.MemberRole;
import com.project.Journey.login.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;


    public Long signUp(MemberDTO dto) {
        if (memberRepository.findByLoginId(dto.getLoginId()).isPresent()) {
            throw new IllegalArgumentException("이미 사용중인 아이디 입니다.");
        }

        if (memberRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 사용중인 이메일 입니다.");
        }

        Member member = Member.builder()
                .loginId(dto.getLoginId())
                .name(dto.getName())
                .nickname(dto.getName())
                .password(passwordEncoder.encode(dto.getPassword()))
                .email(dto.getEmail())
                .role(MemberRole.USER)
                .build();

        // 4) DB 저장
        Member saved = memberRepository.save(member);
        return saved.getId();
    }

    public void updateNickname(Long memberId, String nickname) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다: " + memberId));
        member.setNickname(nickname); // null이 될 수도, 어떤 문자열이 될 수도 있음
        // 변경감지로 DB에 업데이트
    }


}