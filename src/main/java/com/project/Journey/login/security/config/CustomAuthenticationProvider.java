//package com.project.Journey.login.security.config;
//
//import com.project.Journey.login.security.domain.UserDetailsImpl;
//import com.project.Journey.login.security.exception.PasswordNotMatchException;
//import com.project.Journey.login.security.service.UserDetailsServiceImpl;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.authentication.AuthenticationProvider;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//
///**
// * 세션 기반 로그인에서 사용되는 커스텀 AuthenticationProvider
// * - Username(=loginId)와 Password를 받아 DB 검증
// * - 비밀번호 불일치 시 Custom 예외(PasswordNotMatchException) 발생
// */
//@RequiredArgsConstructor
//public class CustomAuthenticationProvider implements AuthenticationProvider {
//
//    // 접근 제어자는 보통 private final로 선언
//    private final UserDetailsServiceImpl userDetailsService;
//    private final BCryptPasswordEncoder passwordEncoder;
//
//    @Override
//    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
//        // 1) Authentication으로부터 principal(username), credentials(password) 추출
//        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;
//        String loginId = token.getName();              // username
//        String rawPassword = (String) token.getCredentials();  // 평문 비밀번호
//
//        // 2) UserDetailsService 통해 DB 사용자 조회
//        UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(loginId);
//
//        // 3) BCryptPasswordEncoder로 비밀번호 검증
//        if (!passwordEncoder.matches(rawPassword, userDetails.getPassword())) {
//            throw new PasswordNotMatchException("비밀번호가 일치하지 않습니다");
//        }
//
//        // 4) 인증 성공 시, 인증된 Authentication 객체 반환
//        return new UsernamePasswordAuthenticationToken(
//                userDetails,     // Principal
//                rawPassword,     // Credentials(원본 PW; 보통 null로 넣기도 함)
//                userDetails.getAuthorities()
//        );
//    }
//
//    /**
//     * 이 Provider가 처리할 수 있는 토큰 타입 설정
//     */
//    @Override
//    public boolean supports(Class<?> authentication) {
//        return authentication.equals(UsernamePasswordAuthenticationToken.class);
//    }
//}
