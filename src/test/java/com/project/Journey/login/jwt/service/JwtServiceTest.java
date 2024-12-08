package com.project.Journey.login.jwt.service;

import com.project.Journey.login.jwt.domain.RefreshToken;
import com.project.Journey.login.jwt.repository.JwtRepository;
import com.project.Journey.login.member.domain.Member;
import com.project.Journey.login.member.domain.MemberRole;
import com.project.Journey.login.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtServiceTest {

    @Mock
    private JwtRepository jwtRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private JwtService jwtService;

    public JwtServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Refresh Token 저장 테스트")
    void testSaveRefreshToken() {
        // Given
        RefreshToken refreshToken = new RefreshToken("sampleToken", "member");
        when(jwtRepository.save(refreshToken)).thenReturn(refreshToken);

        // When
        RefreshToken savedToken = jwtService.save(refreshToken);

        // Then
        assertThat(savedToken).isEqualTo(refreshToken);
        verify(jwtRepository, times(1)).save(refreshToken);
    }

    @Test
    @DisplayName("Refresh Token으로 새로운 Access Token 갱신 테스트")
    void testRenewTokenSuccess() {
        // Given
        RefreshToken refreshToken = new RefreshToken("sampleToken2", "member123");
        Member member = new Member();
        member.setId("member123");
        member.setRole(MemberRole.USER);

        when(jwtRepository.findByToken("sampleToken2")).thenReturn(Optional.of(refreshToken));
        when(memberRepository.findById("member123")).thenReturn(Optional.of(member));

        // When
        String newAccessToken = jwtService.renewToken("sampleToken2");

        // Then
        assertThat(newAccessToken).isNotNull();
    }

    @Test
    @DisplayName("존재하지 않는 Refresh Token으로 갱신 실패 테스트")
    void testRenewTokenFailure() {
        // Given
        when(jwtRepository.findByToken("invalidToken")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NoSuchElementException.class, () -> jwtService.renewToken("invalidToken"));
    }

}