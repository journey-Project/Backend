package com.project.Journey.login.oauth2.utils;

import com.project.Journey.login.member.domain.SocialType;
import com.project.Journey.login.oauth2.userInfo.GoogleOAuth2UserInfo;
import com.project.Journey.login.oauth2.userInfo.OAuth2UserInfo;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class OAuth2UtilsTest {

    @Test
    void testGetSocialType() {
        assertThat(OAuth2Utils.getSocialType("google")).isEqualTo(SocialType.GOOGLE);
        assertThat(OAuth2Utils.getSocialType("kakao")).isEqualTo(SocialType.KAKAO);
        assertThat(OAuth2Utils.getSocialType("naver")).isEqualTo(SocialType.NAVER);
        assertThat(OAuth2Utils.getSocialType("unknown")).isNull();
    }

    @Test
    void testGetOauth2UserInfo() {
        // Given
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("sub", "12345");
        attributes.put("email", "test@example.com");

        // When
        OAuth2UserInfo userInfo = OAuth2Utils.getOauth2UserInfo(SocialType.GOOGLE, attributes);

        // Then
        assertThat(userInfo).isInstanceOf(GoogleOAuth2UserInfo.class);
        assertThat(userInfo.getSocialId()).isEqualTo("12345");
        assertThat(userInfo.getEmail()).isEqualTo("test@example.com");
    }
}