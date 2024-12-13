package com.project.Journey.login.jwt.constants;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.project.Journey.login.member.domain.Member;
import com.project.Journey.login.member.domain.MemberRole;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class JwtUtilsTest {

    @Test
    void testGenerateAndVerifyAccessToken() {
        // Given
        Member member = new Member();
        member.setId("member1");
        member.setRole(MemberRole.USER);

        // When
        String token = JwtUtils.generateAccessToken(member);
        DecodedJWT decodedJWT = JwtUtils.verifyToken(token);

        // Then
        assertThat(decodedJWT.getClaim("id").asString()).isEqualTo("member1");
        assertThat(decodedJWT.getClaim("role").asString()).isEqualTo(MemberRole.USER.getValue());
    }

    @Test
    void testGenerateRefreshToken() {
        // Given
        Member member = new Member();
        member.setId("member2");

        // When
        String refreshToken = JwtUtils.generateRefreshToken(member);

        // Then
        assertThat(refreshToken).isNotNull();
    }

}