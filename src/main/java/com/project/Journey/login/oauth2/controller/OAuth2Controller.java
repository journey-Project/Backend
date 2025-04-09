package com.project.Journey.login.oauth2.controller;

import com.project.Journey.login.auth.CustomUserDetails;
import com.project.Journey.login.member.domain.Member;
import com.project.Journey.login.oauth2.service.OAuth2UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.Collections;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/oauth2")
public class OAuth2Controller {

    private final OAuth2UserService oAuth2UserService;

    /**
     * 인가코드를 프론트에서 받아서, 아래 API로 전달
     * 예: POST /api/oauth2/callback?provider=kakao&code=AAAABBBB
     */
    @PostMapping("/callback")
    public ResponseEntity<?> socialLoginCallback(
            @RequestParam String provider,
            @RequestParam String code,
            HttpSession session
    ) {
        try {
            // 1) DB에서 소셜 사용자 조회/없으면 가입
            Member member = oAuth2UserService.getOrCreateSocialUser(provider, code);

            // 2) "로그인된 상태"로 만들기 (세션 인증)
            //    3-파라미터 생성자 → 이미 인증된 토큰
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            new CustomUserDetails(member),  // principal
                            null,                           // credentials (소셜은 PW 없음)
                            Collections.singletonList(
                                    new SimpleGrantedAuthority("ROLE_" + member.getRole())
                            )
                    );
            // authToken.setAuthenticated(true);  // ← 제거: 이미 인증됨

            // 3) SecurityContextHolder에 인증 저장
            SecurityContextHolder.getContext().setAuthentication(authToken);

            // 4) 세션에 SecurityContext 저장
            session.setAttribute(
                    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    SecurityContextHolder.getContext()
            );

            log.info("[OAuth2Controller] 소셜 로그인 성공: {}", member.getEmail());

            // 5) 응답
            return ResponseEntity.ok("소셜 로그인 성공! Email=" + member.getEmail());

        } catch (Exception e) {
            log.error("소셜 로그인 실패", e);
            return ResponseEntity.badRequest().body("소셜 로그인 실패: " + e.getMessage());
        }
    }
}
