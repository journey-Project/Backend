package com.project.Journey.login.oauth2.service;

import com.project.Journey.login.member.domain.Member;
import com.project.Journey.login.member.domain.MemberRole;
import com.project.Journey.login.member.domain.SocialType;
import com.project.Journey.login.member.repository.MemberRepository;
import com.project.Journey.login.oauth2.domain.OAuth2UserImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistration.ProviderDetails;
import org.springframework.security.oauth2.client.registration.ClientRegistration.ProviderDetails.UserInfoEndpoint;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class OAuth2UserServiceImplTest {

    private OAuth2UserServiceImpl oAuth2UserService;
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        memberRepository = mock(MemberRepository.class);
        oAuth2UserService = new OAuth2UserServiceImpl(memberRepository);
    }

    @Test
    @DisplayName("Google OAuth2 로그인 시 신규 회원이면 GUEST 등급으로 반환")
    void testLoadUser_Google_NewUser() {
        // given
        OAuth2UserRequest userRequest = mock(OAuth2UserRequest.class);
        ClientRegistration clientRegistration = mock(ClientRegistration.class);
        ProviderDetails providerDetails = mock(ProviderDetails.class);
        UserInfoEndpoint userInfoEndpoint = mock(UserInfoEndpoint.class);

        when(userRequest.getClientRegistration()).thenReturn(clientRegistration);
        when(clientRegistration.getRegistrationId()).thenReturn("google");
        when(clientRegistration.getProviderDetails()).thenReturn(providerDetails);
        when(providerDetails.getUserInfoEndpoint()).thenReturn(userInfoEndpoint);
        when(userInfoEndpoint.getUserNameAttributeName()).thenReturn("sub");

        // Mock attributes
        Map<String, Object> attributes = Map.of(
                "sub", "12345",
                "email", "test@example.com"
        );

        // Mock member repository
        Member newMember = Member.builder()
                .socialType(SocialType.GOOGLE)
                .socialId("12345")
                .email("test@example.com")
                .role(MemberRole.GUEST)
                .build();
        when(memberRepository.findBySocialTypeAndSocialId(SocialType.GOOGLE, "12345"))
                .thenReturn(Optional.empty());
        when(memberRepository.save(any(Member.class))).thenReturn(newMember);

        // Mock OAuth2UserImpl
        OAuth2UserImpl oAuth2UserImpl = new OAuth2UserImpl(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_GUEST")),
                attributes,
                "sub",
                newMember
        );

        // Spy the OAuth2UserServiceImpl
        OAuth2UserServiceImpl spyService = Mockito.spy(oAuth2UserService);
        doReturn(oAuth2UserImpl).when(spyService).loadUser(userRequest);

        // when
        OAuth2User result = spyService.loadUser(userRequest);

        // then
        assertNotNull(result);
        assertTrue(result instanceof OAuth2UserImpl);

        OAuth2UserImpl userImpl = (OAuth2UserImpl) result;
        System.out.println("Authorities (New User): " + userImpl.getAuthorities());

        Member member = userImpl.getMember();

        assertEquals("test@example.com", member.getEmail());
        assertEquals(MemberRole.GUEST, member.getRole());
        assertEquals(SocialType.GOOGLE, member.getSocialType());
        assertEquals("12345", member.getSocialId());

        assertTrue(userImpl.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_GUEST")));
    }

    @Test
    @DisplayName("기존 회원일 경우 DB 정보 반영")
    void testLoadUser_Google_ExistingUser() {
        // given
        OAuth2UserRequest userRequest = mock(OAuth2UserRequest.class);
        ClientRegistration clientRegistration = mock(ClientRegistration.class);
        ProviderDetails providerDetails = mock(ProviderDetails.class);
        UserInfoEndpoint userInfoEndpoint = mock(UserInfoEndpoint.class);

        when(userRequest.getClientRegistration()).thenReturn(clientRegistration);
        when(clientRegistration.getRegistrationId()).thenReturn("google");
        when(clientRegistration.getProviderDetails()).thenReturn(providerDetails);
        when(providerDetails.getUserInfoEndpoint()).thenReturn(userInfoEndpoint);
        when(userInfoEndpoint.getUserNameAttributeName()).thenReturn("sub");

        // Mock attributes
        Map<String, Object> attributes = Map.of(
                "sub", "existId",
                "email", "existing@example.com"
        );

        // Mock existing member
        Member existingMember = Member.builder()
                .socialType(SocialType.GOOGLE)
                .socialId("existId")
                .email("existing@example.com")
                .role(MemberRole.USER)
                .build();
        when(memberRepository.findBySocialTypeAndSocialId(SocialType.GOOGLE, "existId"))
                .thenReturn(Optional.of(existingMember));

        // Mock OAuth2UserImpl
        OAuth2UserImpl oAuth2UserImpl = new OAuth2UserImpl(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                attributes,
                "sub",
                existingMember
        );

        // Spy the OAuth2UserServiceImpl
        OAuth2UserServiceImpl spyService = Mockito.spy(oAuth2UserService);
        doReturn(oAuth2UserImpl).when(spyService).loadUser(userRequest);

        // when
        OAuth2User result = spyService.loadUser(userRequest);

        // then
        assertNotNull(result);
        assertTrue(result instanceof OAuth2UserImpl);

        OAuth2UserImpl userImpl = (OAuth2UserImpl) result;
        System.out.println("Authorities (Existing User): " + userImpl.getAuthorities());

        Member member = userImpl.getMember();

        assertEquals("existing@example.com", member.getEmail());
        assertEquals(MemberRole.USER, member.getRole());
        assertEquals(SocialType.GOOGLE, member.getSocialType());
        assertEquals("existId", member.getSocialId());

        assertTrue(userImpl.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    @DisplayName("소셜 로그인 실패 시 OAuth2AuthenticationException 발생")
    void testLoadUser_AuthenticationFailure() {
        // Spy OAuth2UserServiceImpl
        OAuth2UserServiceImpl spyService = spy(oAuth2UserService);

        OAuth2UserRequest userRequest = mock(OAuth2UserRequest.class);

        // Mock loadUser to throw an exception
        doThrow(new OAuth2AuthenticationException("Authentication failed"))
                .when(spyService).loadUser(userRequest);

        // when & then
        assertThrows(OAuth2AuthenticationException.class, () -> {
            spyService.loadUser(userRequest);
        });
    }
}