//package com.project.Journey.login.jwt.service;
//
//import com.project.Journey.login.jwt.constants.JwtUtils;
//import com.project.Journey.login.jwt.domain.RefreshToken;
//import com.project.Journey.login.jwt.repository.JwtRepository;
//import com.project.Journey.login.member.domain.Member;
//import com.project.Journey.login.member.repository.MemberRepository;
//import com.project.Journey.login.security.exception.UserNotFoundException;
//import lombok.RequiredArgsConstructor;
//
//import org.springframework.stereotype.Service;
//
//import java.util.NoSuchElementException;
//import java.util.Optional;
//
//@Service
//@RequiredArgsConstructor
//public class JwtService {
//    private final JwtRepository jwtRepository;
//    private final MemberRepository memberRepository;
//
//    public RefreshToken save(RefreshToken refreshToken) {
//        return jwtRepository.save(refreshToken);
//    }
//
//    public Optional<RefreshToken> findByToken(String token){
//        return jwtRepository.findByToken(token);
//    }
//
//    public String renewToken(String refreshToken){
//        //token 이 존재하는지 찾고, 존재한다면 RefreshToken 안의 memberId 를 가져와서 member 을 찾은 후 AccessToken 생성
//        RefreshToken token = this.findByToken(refreshToken).orElseThrow(NoSuchElementException::new);
//        Member member = memberRepository.findById(token.getMemberId()).orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다"));
//        return JwtUtils.generateAccessToken(member);
//    }
//
//}
