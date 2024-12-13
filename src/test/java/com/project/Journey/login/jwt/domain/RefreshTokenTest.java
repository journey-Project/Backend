package com.project.Journey.login.jwt.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class RefreshTokenTest {
    @Test
    @DisplayName("Refresh Token 객체 생성 테스트")
    void testRefreshToken() {
        RefreshToken refreshToken = new RefreshToken("sampleToken", "member1");

        assertThat(refreshToken.getToken()).isEqualTo("sampleToken");
        assertThat(refreshToken.getMemberId()).isEqualTo("member1");
    }

}