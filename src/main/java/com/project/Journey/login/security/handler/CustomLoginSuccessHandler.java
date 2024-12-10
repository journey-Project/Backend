package com.project.Journey.login.security.handler;

import com.project.Journey.login.jwt.constants.JwtConstants;
import com.project.Journey.login.jwt.constants.JwtUtils;
import com.project.Journey.login.jwt.domain.RefreshToken;
import com.project.Journey.login.jwt.service.JwtService;
import com.project.Journey.login.member.domain.Member;
import com.project.Journey.login.security.domain.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import java.io.IOException;

@RequiredArgsConstructor
public class CustomLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final JwtService jwtService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        // 인증 성공 후 처리되는 핸들러이기 때문에 전달받은 authentication 은 인증이 완료된 객체
        Member member = ((UserDetailsImpl) authentication.getPrincipal()).getMember();

        // 인증이 성공한 경우 토큰을 생성하여, 응답 헤더에 담아 클라이언트에게 전달
        String accessToken = JwtUtils.generateAccessToken(member);
        String refreshToken = JwtUtils.generateRefreshToken(member);

        // 인증이 성공했으니 Refresh Token dmf DB ( Redis )에 저장
        jwtService.save(new RefreshToken(refreshToken, member.getId()));

        // 헤더로 accessToken 전달
        response.addHeader(JwtConstants.ACCESS, JwtConstants.JWT_TYPE + accessToken);
        response.addHeader(JwtConstants.REFRESH, JwtConstants.JWT_TYPE + refreshToken);
    }
}
