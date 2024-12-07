package com.project.Journey.login.oauth2.domain;

import com.project.Journey.login.member.domain.SocialType;
import com.project.Journey.login.oauth2.userInfo.Oauth2UserInfo;
import com.project.Journey.login.oauth2.utils.Oauth2Utils;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
public class Oauth2Attributes {
    private String nameAttributeKey;
    private Oauth2UserInfo oauth2UserInfo;

    @Builder
    public Oauth2Attributes(String nameAttributeKey, Oauth2UserInfo oauth2UserInfo){
        this.nameAttributeKey = nameAttributeKey;
        this.oauth2UserInfo = oauth2UserInfo;
    }

    //Oauth2Utils를 통해 분기처리 없이 생성된 Oauth2UserInfo를 반환
    public static Oauth2Attributes of(SocialType socialType, String userNameAttributeName,
                                      Map<String, Object> attributes) {
        return Oauth2Attributes.builder()
                .nameAttributeKey(userNameAttributeName)
                .oauth2UserInfo(Oauth2Utils.getOauth2UserInfo(socialType, attributes))
                .build();
    }
}
