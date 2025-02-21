//package com.project.Journey.login.oauth2.handler;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.project.Journey.login.jwt.constants.JwtConstants;
//import com.project.Journey.login.jwt.constants.JwtUtils;
//import com.project.Journey.login.jwt.domain.RefreshToken;
//import com.project.Journey.login.jwt.service.JwtService;
//import com.project.Journey.login.member.domain.Member;
//import com.project.Journey.login.member.domain.MemberRole;
//import com.project.Journey.login.oauth2.domain.OAuth2UserImpl;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
//
//import java.io.IOException;
//import java.net.URLEncoder;
//import java.nio.charset.StandardCharsets;
//import java.util.HashMap;
//import java.util.Map;
//
//@RequiredArgsConstructor
//public class CustomOAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
//
//    private final JwtService jwtService;
//
//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
//        Member member = ((OAuth2UserImpl) authentication.getPrincipal()).getMember();
//        String accessToken = JwtUtils.generateAccessToken(member);
//
//        if (member.getRole().equals(MemberRole.GUEST)) {
//            response.addHeader(JwtConstants.ACCESS, JwtConstants.JWT_TYPE + accessToken);
//
////            Map<String, String> responseBody = new HashMap<>();
////            responseBody.put("redirectUrl", "/api/auth/sign-up");
////            responseBody.put("email", member.getEmail());
////            responseBody.put("socialType", member.getSocialType().toString());
////            responseBody.put("socialId", member.getSocialId());
////
////            response.setContentType("application/json");
////            response.setCharacterEncoding("utf-8");
////            new ObjectMapper().writeValue(response.getWriter(), responseBody);
//
//            // 1) 프론트 회원가입 페이지 URL
//            //    여기서 파라미터로 email, socialType, socialId를 전달
//            String redirectUrl = "https://dxkiwmo9p9ise.cloudfront.net/" // 임시
//                    + "?email=" + URLEncoder.encode(member.getEmail(), StandardCharsets.UTF_8)
//                    + "&socialType=" + member.getSocialType()
//                    + "&socialId=" + member.getSocialId();
//
//            // 2) 302 Redirect
//            response.sendRedirect(redirectUrl);
//        } else {
//            String refreshToken = JwtUtils.generateRefreshToken(member);
//            jwtService.save(new RefreshToken(refreshToken, member.getId()));
//
//            response.addHeader(JwtConstants.ACCESS, JwtConstants.JWT_TYPE + accessToken);
//            response.addHeader(JwtConstants.REFRESH, JwtConstants.JWT_TYPE + refreshToken);
//
////            Map<String, String> responseBody = new HashMap<>();
////            responseBody.put("redirectUrl", "/loginSuccess");
////
////            response.setContentType("application/json");
////            response.setCharacterEncoding("utf-8");
////            new ObjectMapper().writeValue(response.getWriter(), responseBody);
//
//            // 로그인 성공 후 프론트 홈 화면으로 리다이렉트
//            response.sendRedirect("https://dxkiwmo9p9ise.cloudfront.net/");
//        }
//    }
//}
