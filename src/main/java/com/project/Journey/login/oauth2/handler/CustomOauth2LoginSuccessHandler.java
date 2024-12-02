package com.project.Journey.login.oauth2.handler;

import com.nimbusds.oauth2.sdk.token.RefreshToken;
import com.project.Journey.login.jwt.constants.JwtConstants;
import com.project.Journey.login.jwt.constants.JwtUtils;
import com.project.Journey.login.jwt.service.JwtService;
import com.project.Journey.login.member.domain.Member;
import com.project.Journey.login.member.domain.MemberRole;
import com.project.Journey.login.oauth2.domain.Oauth2UserImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
public class CustomOauth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        Member member = ((Oauth2UserImpl) authentication.getPrincipal()).getMember();
        String accessToken = JwtUtils.generateAccessToken(member);

        //최초 로그인한 경우 추가 정보 입력을 위한 회원가입 페이지로 리다이렉트
        if(member.getRole().equals((MemberRole.GUEST))) {
            response.addHeader(JwtConstants.ACCESS, JwtConstants.JWT_TYPE + accessToken);
            //임시 URL http://localhost:8080/oauth2/signUp
            String redirectURL = UriComponentsBuilder.fromUriString("http://localhost:8080/oauth2/signUp")
                    .queryParam("email", member.getEmail())
                    .queryParam("socialType", member.getSocialType())
                    .queryParam("socialId", member.getSocialId())
                    .build()
                    .encode(StandardCharsets.UTF_8)
                    .toUriString();
            getRedirectStrategy().sendRedirect(request,response,redirectURL);
        } else {
            String refreshToken = JwtUtils.generateRefreshToken(member);
            JwtService.save(new RefreshToken(refreshToken, member.getId()));

            response.addHeader(JwtConstants.ACCESS, JwtConstants.JWT_TYPE + accessToken);
            response.addHeader(JwtConstants.REFRESH, JwtConstants.JWT_TYPE + refreshToken);

            // 최초 로그인인 아닌 경우에는 로그인 성공 페이지로 이동
            String redirectURL = UriComponentsBuilder.fromUriString("\"http://localhost:8080/loginSuccess")
                    .build()
                    .encode(StandardCharsets.UTF_8)
                    .toUriString();
            getRedirectStrategy().sendRedirect(request, response, redirectURL);
        }
    }


}
