package com.project.Journey.login.member.service;

import com.project.Journey.login.member.domain.Member;
import com.project.Journey.login.member.dto.MemberDTO;
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

        String defaultProfileImageUrl = "https://journeybucket0.s3.ap-northeast-2.amazonaws.com/USER/5c380987-c103-4ed5-ae55-0baef59574b7.jpeg";

        Member member = Member.builder()
                .loginId(dto.getLoginId())
                .name(dto.getName())
                .nickname(dto.getName())
                .password(passwordEncoder.encode(dto.getPassword()))
                .email(dto.getEmail())
                .profileImage(defaultProfileImageUrl)
                .role(MemberRole.USER)
                .build();

        // 4) DB 저장
        Member saved = memberRepository.save(member);
        return saved.getId();
    }

    public void updateNickname(Long memberId, String nickname) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다: " + memberId));

        if (nickname == null || nickname.isBlank()) {
            member.setNickname(member.getName());  // 기본값으로 실명 설정
        } else {
            member.setNickname(nickname);
        }
    }


}