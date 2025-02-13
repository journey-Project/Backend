//package com.project.Journey.login.oauth2.domain;
//
//import com.project.Journey.login.member.domain.Member;
//import lombok.Getter;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
//
//import java.util.Collection;
//import java.util.Map;
//
//@Getter
//public class OAuth2UserImpl extends DefaultOAuth2User {
//
//    Member member;
//
//    public OAuth2UserImpl(Collection<? extends GrantedAuthority> authorities,
//                          Map<String, Object> attributes, String nameAttributeKey,
//                          Member member){
//        super(authorities, attributes, nameAttributeKey);
//        this.member = member;
//
//    }
//}
