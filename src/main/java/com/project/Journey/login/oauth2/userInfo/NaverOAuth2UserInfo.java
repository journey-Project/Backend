package com.project.Journey.login.oauth2.userInfo;

import java.util.Map;

public class NaverOAuth2UserInfo extends OAuth2UserInfo {

    private final Map<String, Object> responseMap; // static → instance 필드

    public NaverOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
        this.responseMap = (Map<String, Object>) attributes.get("response");
    }

    @Override
    public String getSocialId() {
        return String.valueOf(responseMap.get("id"));
    }

    @Override
    public String getEmail() {
        return String.valueOf(responseMap.get("email"));
    }
}
