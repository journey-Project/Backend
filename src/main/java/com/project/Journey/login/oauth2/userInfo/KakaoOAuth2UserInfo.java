package com.project.Journey.login.oauth2.userInfo;

import java.util.Map;

public class KakaoOAuth2UserInfo extends OAuth2UserInfo {

    // ★ 인스턴스 필드로 변경
    private final Map<String, Object> account;
    private final Map<String, Object> profile;

    public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
        this.account  = (Map<String, Object>) attributes.getOrDefault("kakao_account", Map.of());
        this.profile  = (Map<String, Object>)  account.getOrDefault("profile", Map.of());
    }

    @Override
    public String getSocialId() {
        Object id = attributes.get("id");
        if (id == null) {
            throw new IllegalStateException("Kakao userinfo에 id가 없습니다");
        }
        return String.valueOf(id);
    }

    @Override
    public String getEmail() {
        return (String) account.get("email");   // null이면 그대로 null 반환
    }
}

