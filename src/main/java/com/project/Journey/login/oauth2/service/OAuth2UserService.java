package com.project.Journey.login.oauth2.service;

import com.project.Journey.login.member.domain.Member;
import com.project.Journey.login.member.domain.MemberRole;
import com.project.Journey.login.member.domain.SocialType;
import com.project.Journey.login.member.repository.MemberRepository;
import com.project.Journey.login.oauth2.userInfo.GoogleOAuth2UserInfo;
import com.project.Journey.login.oauth2.userInfo.KakaoOAuth2UserInfo;
import com.project.Journey.login.oauth2.userInfo.NaverOAuth2UserInfo;
import com.project.Journey.login.oauth2.userInfo.OAuth2UserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class OAuth2UserService {

    private final MemberRepository memberRepository;

    // ==== 소셜 설정 값들 ====
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

    public Member getOrCreateSocialUser(String provider, String code) {
        SocialType socialType = SocialType.valueOf(provider.toUpperCase());
        OAuth2UserInfo userInfo = getUserInfoByProvider(socialType, code);

        Member member = findOrCreateMember(socialType, userInfo);

        return member;
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

    private Member findOrCreateMember(SocialType socialType, OAuth2UserInfo userInfo) {
        String socialId = userInfo.getSocialId();
        String email = userInfo.getEmail();

        Optional<Member> optional = memberRepository.findBySocialTypeAndSocialId(socialType, socialId);

        if (optional.isPresent()) {
            log.info("[OAuth2UserService] 기존 소셜회원: {}, {}", socialType, email);
            return optional.get();
        } else {
            // 신규 가입
            Member newMember = Member.builder()
                    .socialType(socialType)
                    .socialId(socialId)
                    .email(email)
                    .role(MemberRole.USER)
                    .build();

            memberRepository.save(newMember);
            log.info("[OAuth2UserService] 새 소셜회원 가입: {}, {}", socialType, email);
            return newMember;
        }
    }

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
                            .queryParam("client_secret", KAKAO_CLIENT_SECRET)
                            .queryParam("code", code)
                            .build()
                    )
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .retrieve()
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
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block(Duration.ofSeconds(5));
        } catch (WebClientResponseException e) {
            log.error("Kakao userinfo request fail: status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Kakao userinfo request fail", e);
        }
    }

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
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block(Duration.ofSeconds(5));
        } catch (WebClientResponseException e) {
            log.error("Naver userinfo request fail: status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Naver userinfo request fail", e);
        }
    }

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
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block(Duration.ofSeconds(5));
        } catch (WebClientResponseException e) {
            log.error("Google userinfo request fail: status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Google userinfo request fail", e);
        }
    }
}
