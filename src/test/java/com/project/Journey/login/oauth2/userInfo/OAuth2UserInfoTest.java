package com.project.Journey.login.oauth2.userInfo;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class OAuth2UserInfoTest {

    @Test
    void testGoogleOauth2UserInfoParsing() {
        // Given
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("sub", "12345");
        attributes.put("email", "test@example.com");
        attributes.put("name", "Test User");

        GoogleOAuth2UserInfo userInfo = new GoogleOAuth2UserInfo(attributes);

        // Then
        assertThat(userInfo.getSocialId()).isEqualTo("12345");
        assertThat(userInfo.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void testKakaoOauth2UserInfo() {
        // Given
        Map<String, Object> attributes = new HashMap<>();
        Map<String, Object> kakaoAccount = new HashMap<>();
        Map<String, Object> profile = new HashMap<>();

        profile.put("nickname", "Kakao User");
        kakaoAccount.put("email", "kakao@example.com");
        kakaoAccount.put("profile", profile);
        attributes.put("id", "12345");
        attributes.put("kakao_account", kakaoAccount);

        // When
        KakaoOAuth2UserInfo userInfo = new KakaoOAuth2UserInfo(attributes);

        // Then
        assertThat(userInfo.getSocialId()).isEqualTo("12345");
        assertThat(userInfo.getEmail()).isEqualTo("kakao@example.com");
    }

    @Test
    void testNaverOauth2UserInfo() {
        // Given
        Map<String, Object> attributes = new HashMap<>();
        Map<String, Object> response = new HashMap<>();

        response.put("id", "54321");
        response.put("email", "naver@example.com");
        attributes.put("response", response);

        // When
        NaverOAuth2UserInfo userInfo = new NaverOAuth2UserInfo(attributes);

        // Then
        assertThat(userInfo.getSocialId()).isEqualTo("54321");
        assertThat(userInfo.getEmail()).isEqualTo("naver@example.com");
    }
}
