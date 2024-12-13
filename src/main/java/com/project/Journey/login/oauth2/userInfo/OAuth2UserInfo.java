package com.project.Journey.login.oauth2.userInfo;

import java.util.Map;

public abstract class OAuth2UserInfo {

    protected Map<String, Object> attributes;

    public OAuth2UserInfo(Map<String, Object> attributes){
        this.attributes = attributes;
    }

    // 소셜에서 제공받은 정보를 추출하기 위한 메서드
    public abstract String getSocialId();
    public abstract String getEmail();
}
