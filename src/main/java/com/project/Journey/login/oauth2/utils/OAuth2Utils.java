package com.project.Journey.login.oauth2.utils;

import com.project.Journey.login.member.domain.SocialType;
import com.project.Journey.login.oauth2.userInfo.GoogleOAuth2UserInfo;
import com.project.Journey.login.oauth2.userInfo.KakaoOAuth2UserInfo;
import com.project.Journey.login.oauth2.userInfo.NaverOAuth2UserInfo;
import com.project.Journey.login.oauth2.userInfo.OAuth2UserInfo;

import java.util.Map;

// 소셜 타입에 따른 모든 분기처리를 진행하는 클래스
public class OAuth2Utils {

    // registrationID를 보고 어떤 소셜에서 인증했는지 반환
    public static SocialType getSocialType(String registrationId){
        // Member에서도 사용하기 위하여 대문자로 변경
        if(registrationId != null){
            registrationId = registrationId.toUpperCase();
        }

        if("GOOGLE".equals(registrationId)){
            return SocialType.GOOGLE;
        } else if ("KAKAO".equals(registrationId)) {
            return SocialType.KAKAO;
        } else if ("NAVER".equals(registrationId)) {
            return SocialType.NAVER;
        }
        return null;
    }

    // 소셜로로부터 받은 정보들을 받아 파싱하는 Oauth2UserInfo를 생성하는 메서드
    public static OAuth2UserInfo getOauth2UserInfo(SocialType socialType, Map<String, Object> attributes) {
        switch (socialType) {
            case GOOGLE:
                return new GoogleOAuth2UserInfo(attributes);
            case NAVER:
                return new NaverOAuth2UserInfo(attributes);
            case KAKAO:
                return new KakaoOAuth2UserInfo(attributes);
        }
        return null;

    }
}
