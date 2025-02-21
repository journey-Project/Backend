package com.project.Journey.login.oauth2.userInfo;

import java.util.Map;

public class KakaoOAuth2UserInfo extends OAuth2UserInfo {

    // ★ 인스턴스 필드로 변경
    private final Map<String, Object> account;
    private final Map<String, Object> profile;

    public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
        this.account = (Map<String, Object>) attributes.get("kakao_account");
        if (account != null) {
            this.profile = (Map<String, Object>) account.get("profile");
        } else {
            this.profile = null;
        }
    }

    @Override
    public String getSocialId() {
        return String.valueOf(attributes.get("id"));
    }

    @Override
    public String getEmail() {
        if (account != null) {
            return (String) account.get("email");
        }
        return null;
    }
}

