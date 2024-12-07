package com.project.Journey.login.oauth2.userInfo;

import java.util.Map;

public class GoogleOauth2UserInfo extends Oauth2UserInfo {
    public GoogleOauth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getSocialId() {
        return String.valueOf(attributes.get("sub"));
    }

    @Override
    public String getEmail(){
        return String.valueOf(attributes.get("email"));
    }
}
