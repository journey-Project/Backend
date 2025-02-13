//package com.project.Journey.login.oauth2.domain;
//
//import com.project.Journey.login.member.domain.SocialType;
//import com.project.Journey.login.oauth2.userInfo.GoogleOAuth2UserInfo;
//import org.junit.jupiter.api.Test;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//class OAuth2AttributesTest {
//
//    @Test
//    void testOauth2AttributesOf() {
//        // Given
//        Map<String, Object> attributes = new HashMap<>();
//
//        attributes.put("sub", "12345");
//        attributes.put("email", "test@example.com");
//        attributes.put("name", "Test User");
//
//        // When
//        OAuth2Attributes oauth2Attributes = OAuth2Attributes.of(SocialType.GOOGLE, "sub", attributes);
//
//        // Then
//        assertThat(oauth2Attributes.getNameAttributeKey()).isEqualTo("sub");
//        assertThat(oauth2Attributes.getOauth2UserInfo()).isInstanceOf(GoogleOAuth2UserInfo.class);
//    }
//
//}