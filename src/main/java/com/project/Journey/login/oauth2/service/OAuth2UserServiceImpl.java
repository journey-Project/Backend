package com.project.Journey.login.oauth2.service;

import com.project.Journey.login.jwt.constants.JwtConstants;
import com.project.Journey.login.jwt.constants.JwtUtils;
import com.project.Journey.login.jwt.domain.RefreshToken;
import com.project.Journey.login.jwt.service.JwtService;
import com.project.Journey.login.member.domain.Member;
import com.project.Journey.login.member.domain.MemberRole;
import com.project.Journey.login.member.domain.SocialType;
import com.project.Journey.login.member.repository.MemberRepository;
import com.project.Journey.login.oauth2.userInfo.GoogleOAuth2UserInfo;
import com.project.Journey.login.oauth2.userInfo.KakaoOAuth2UserInfo;
import com.project.Journey.login.oauth2.userInfo.NaverOAuth2UserInfo;
import com.project.Journey.login.oauth2.userInfo.OAuth2UserInfo;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2UserServiceImpl {

    private final MemberRepository memberRepository;
    private final JwtService jwtService;

    @Value("${social.kakao.client-id}")
    private String KAKAO_CLIENT_ID;
    @Value("${social.kakao.client-secret:}")
    private String KAKAO_CLIENT_SECRET;
    @Value("${social.kakao.redirect-uri}")
    private String KAKAO_REDIRECT_URI;

    @Value("${social.naver.client-id}")
    private String NAVER_CLIENT_ID;
    @Value("${social.naver.client-secret}")
    private String NAVER_CLIENT_SECRET;
    @Value("${social.naver.redirect-uri}")
    private String NAVER_REDIRECT_URI;

    @Value("${social.google.client-id}")
    private String GOOGLE_CLIENT_ID;
    @Value("${social.google.client-secret}")
    private String GOOGLE_CLIENT_SECRET;
    @Value("${social.google.redirect-uri}")
    private String GOOGLE_REDIRECT_URI;


    public Map<String, Object> oauthLogin(String provider, String code, HttpServletResponse response) {
        log.info("OAuth 요청 시작: provider={}, code={}", provider, code);

        // 중복 요청 방지 (이미 응답을 보냈다면 return)
        if (response.isCommitted()) {
            log.warn("이미 응답이 완료된 요청입니다. 중복 요청 방지.");
            return null;
        }

        SocialType socialType = SocialType.valueOf(provider.toUpperCase());
        OAuth2UserInfo userInfo = getUserInfoByProvider(socialType, code);
        String socialId = userInfo.getSocialId();
        String email = userInfo.getEmail();
        log.info("[oauthLogin] provider={}, socialId={}, email={}", provider, socialId, email);

        Optional<Member> optionalMember = memberRepository.findBySocialTypeAndSocialId(socialType, socialId);

        Member member;
        if (optionalMember.isEmpty()) {
            member = Member.builder()
                    .email(email)
                    .socialId(socialId)
                    .socialType(socialType)
                    .role(MemberRole.USER)
                    .build();

            //DB저장
            memberRepository.save(member);

            log.info("새 소셜사용자 가입: socialId={}, email={}", socialId, email);
        } else {
            member = optionalMember.get();
            log.info("기존 소셜사용자 로그인: socialId={}, email={}", socialId, email);
        }


        // member는 role=USER 상태
        // JWT 발급 + 쿠키 설정 바로 하기
        String accessToken = JwtUtils.generateAccessToken(member);
        String refreshToken = JwtUtils.generateRefreshToken(member);

        // RefreshToken DB 저장
        jwtService.save(new RefreshToken(refreshToken, member.getId()));

        /*
        // 쿠키 생성
        Cookie accessCookie = new Cookie("accessToken", accessToken);
        accessCookie.setSecure(false);
        accessCookie.setHttpOnly(false);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(60 * 30);
        */
        // -----------------------------------------------------------
        // (수정) 직접 Set-Cookie 헤더로 SameSite=None 추가
        // -----------------------------------------------------------
        boolean isLocal = true; // 로컬환경이면 true, 운영환경(HTTPS)이면 false

        int accessMaxAge = 60 * 30;  // 30분
        int refreshMaxAge = 60 * 60 * 24 * 7;  // 7일

        // Access Token 쿠키
        // ----- Access Token 쿠키 -----
        StringBuilder accessCookieVal = new StringBuilder();
        accessCookieVal.append("accessToken=").append(accessToken)
                .append("; Max-Age=").append(accessMaxAge)
                .append("; Path=/")
                .append("; HttpOnly");

        if (isLocal) {
            accessCookieVal.append("; SameSite=Lax");
        } else {
            accessCookieVal.append("; SameSite=None; Secure");
        }

        response.addHeader("Set-Cookie", accessCookieVal.toString());

        // ----- Refresh Token 쿠키 -----
        StringBuilder refreshCookieVal = new StringBuilder();
        refreshCookieVal.append("refreshToken=").append(refreshToken)
                .append("; Max-Age=").append(refreshMaxAge)
                .append("; Path=/")
                .append("; HttpOnly");

        if (isLocal) {
            refreshCookieVal.append("; SameSite=Lax");
        } else {
            refreshCookieVal.append("; SameSite=None; Secure");
        }

        response.addHeader("Set-Cookie", refreshCookieVal.toString());

        // -----------------------------
        // 응답 JSON
        // -----------------------------
        String status = optionalMember.isEmpty() ? "NEW_USER" : "EXIST";

        return Map.of(
                "status", status,
                "message", "소셜 로그인 성공",
                "email", member.getEmail(),
                "role", member.getRole().name()
        );
    }

    private OAuth2UserInfo getUserInfoByProvider(SocialType provider, String code) {
        switch (provider) {
            case KAKAO:
                return getKakaoUserInfo(code);
            case NAVER:
                return getNaverUserInfo(code);
            case GOOGLE:
                return getGoogleUserInfo(code);
            default:
                throw new IllegalArgumentException("지원하지 않는 소셜: " + provider);
        }
    }

    // ================== KAKAO ==================
    private OAuth2UserInfo getKakaoUserInfo(String code) {
        Map<String, Object> tokenMap = tokenRequestKakao(code);
        String accessToken = (String) tokenMap.get("access_token");

        Map<String, Object> userMap = userInfoRequestKakao(accessToken);
        return new KakaoOAuth2UserInfo(userMap);
    }

    private Map<String, Object> tokenRequestKakao(String code) {
        try {
            return WebClient.create("https://kauth.kakao.com")
                    .post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/oauth/token")
                            .queryParam("grant_type", "authorization_code")
                            .queryParam("client_id", KAKAO_CLIENT_ID)
                            .queryParam("redirect_uri", KAKAO_REDIRECT_URI)
                            // 필요 시 client_secret
                            .queryParam("client_secret", KAKAO_CLIENT_SECRET)
                            .queryParam("code", code)
                            .build()
                    )
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .retrieve()
                    .onStatus(status -> status.isError(), response ->
                            // 반환타입: Function<ClientResponse, Mono<? extends Throwable>>
                            response.bodyToMono(String.class)
                                    .map(errorBody -> {
                                        log.error("[Kakao token error] body={}", errorBody);
                                        return new RuntimeException("Kakao token API error: " + errorBody);
                                    })
                    )
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block(Duration.ofSeconds(5));
        } catch (WebClientResponseException e) {
            log.error("Kakao token request fail: status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Kakao token request fail", e);
        }
    }

    private Map<String, Object> userInfoRequestKakao(String accessToken) {
        try {
            return WebClient.create("https://kapi.kakao.com")
                    .get()
                    .uri("/v2/user/me")
                    .header("Authorization", "Bearer " + accessToken)
                    .retrieve()
                    .onStatus(status -> status.isError(), response ->
                            response.bodyToMono(String.class)
                                    .map(errorBody -> {
                                        log.error("[Kakao userinfo error] body={}", errorBody);
                                        return new RuntimeException("Kakao userinfo API error: " + errorBody);
                                    })
                    )
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block(Duration.ofSeconds(5));
        } catch (WebClientResponseException e) {
            log.error("Kakao userinfo request fail: status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Kakao userinfo request fail", e);
        }
    }

    // ================== NAVER ==================
    private OAuth2UserInfo getNaverUserInfo(String code) {
        Map<String, Object> tokenMap = tokenRequestNaver(code);
        String accessToken = (String) tokenMap.get("access_token");

        Map<String, Object> userMap = userInfoRequestNaver(accessToken);
        return new NaverOAuth2UserInfo(userMap);
    }

    private Map<String, Object> tokenRequestNaver(String code) {
        try {
            return WebClient.create("https://nid.naver.com")
                    .post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/oauth2.0/token")
                            .queryParam("grant_type", "authorization_code")
                            .queryParam("client_id", NAVER_CLIENT_ID)
                            .queryParam("client_secret", NAVER_CLIENT_SECRET)
                            .queryParam("redirect_uri", NAVER_REDIRECT_URI)
                            .queryParam("code", code)
                            .build())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .retrieve()
                    .onStatus(status -> status.isError(), response ->
                            response.bodyToMono(String.class)
                                    .map(errorBody -> {
                                        log.error("[Naver token error] body={}", errorBody);
                                        return new RuntimeException("Naver token API error: " + errorBody);
                                    })
                    )
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block(Duration.ofSeconds(5));
        } catch (WebClientResponseException e) {
            log.error("Naver token request fail: status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Naver token request fail", e);
        }
    }

    private Map<String, Object> userInfoRequestNaver(String accessToken) {
        try {
            return WebClient.create("https://openapi.naver.com")
                    .get()
                    .uri("/v1/nid/me")
                    .header("Authorization", "Bearer " + accessToken)
                    .retrieve()
                    .onStatus(status -> status.isError(), response ->
                            response.bodyToMono(String.class)
                                    .map(errorBody -> {
                                        log.error("[Naver userinfo error] body={}", errorBody);
                                        return new RuntimeException("Naver userinfo API error: " + errorBody);
                                    })
                    )
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block(Duration.ofSeconds(5));
        } catch (WebClientResponseException e) {
            log.error("Naver userinfo request fail: status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Naver userinfo request fail", e);
        }
    }

    // ================== GOOGLE ==================
    private OAuth2UserInfo getGoogleUserInfo(String code) {
        Map<String, Object> tokenMap = tokenRequestGoogle(code);
        String accessToken = (String) tokenMap.get("access_token");

        Map<String, Object> userMap = userInfoRequestGoogle(accessToken);
        return new GoogleOAuth2UserInfo(userMap);
    }

    private Map<String, Object> tokenRequestGoogle(String code) {
        try {
            return WebClient.create("https://oauth2.googleapis.com")
                    .post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/token")
                            .queryParam("grant_type", "authorization_code")
                            .queryParam("client_id", GOOGLE_CLIENT_ID)
                            .queryParam("client_secret", GOOGLE_CLIENT_SECRET)
                            .queryParam("redirect_uri", GOOGLE_REDIRECT_URI)
                            .queryParam("code", code)
                            .build())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .retrieve()
                    .onStatus(status -> status.isError(), response ->
                            response.bodyToMono(String.class)
                                    .map(errorBody -> {
                                        log.error("[Google token error] body={}", errorBody);
                                        return new RuntimeException("Google token API error: " + errorBody);
                                    })
                    )
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block(Duration.ofSeconds(5));
        } catch (WebClientResponseException e) {
            log.error("Google token request fail: status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Google token request fail", e);
        }
    }

    private Map<String, Object> userInfoRequestGoogle(String accessToken) {
        try {
            return WebClient.create("https://www.googleapis.com")
                    .get()
                    .uri("/oauth2/v2/userinfo")
                    .header("Authorization", "Bearer " + accessToken)
                    .retrieve()
                    .onStatus(status -> status.isError(), response ->
                            response.bodyToMono(String.class)
                                    .map(errorBody -> {
                                        log.error("[Google userinfo error] body={}", errorBody);
                                        return new RuntimeException("Google userinfo API error: " + errorBody);
                                    })
                    )
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block(Duration.ofSeconds(5));
        } catch (WebClientResponseException e) {
            log.error("Google userinfo request fail: status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Google userinfo request fail", e);
        }
    }
}
