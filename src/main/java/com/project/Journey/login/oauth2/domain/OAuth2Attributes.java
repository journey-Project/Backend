package com.project.Journey.login.oauth2.domain;

import com.project.Journey.login.member.domain.SocialType;
import com.project.Journey.login.oauth2.userInfo.OAuth2UserInfo;
import com.project.Journey.login.oauth2.utils.OAuth2Utils;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
public class OAuth2Attributes {
    private String nameAttributeKey;
    private OAuth2UserInfo oauth2UserInfo;

    @Builder
    public OAuth2Attributes(String nameAttributeKey, OAuth2UserInfo oauth2UserInfo){
        this.nameAttributeKey = nameAttributeKey;
        this.oauth2UserInfo = oauth2UserInfo;
    }

    //Oauth2Utils를 통해 분기처리 없이 생성된 Oauth2UserInfo를 반환
    public static OAuth2Attributes of(SocialType socialType, String userNameAttributeName,
                                      Map<String, Object> attributes) {
        return OAuth2Attributes.builder()
                .nameAttributeKey(userNameAttributeName)
                .oauth2UserInfo(OAuth2Utils.getOauth2UserInfo(socialType, attributes))
                .build();
    }
}
