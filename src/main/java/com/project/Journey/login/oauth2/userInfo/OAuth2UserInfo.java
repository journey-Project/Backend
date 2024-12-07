package com.project.Journey.login.oauth2.userInfo;

import java.util.Map;
import java.util.Objects;

public abstract class Oauth2UserInfo {

    protected Map<String, Object> attributes;

    public Oauth2UserInfo(Map<String, Object> attributes){
        this.attributes = attributes;
    }

    // 소셜에서 제공받은 정보를 추출하기 위한 메서드
    public abstract String getSocialId();
    public abstract String getEmail();
}
