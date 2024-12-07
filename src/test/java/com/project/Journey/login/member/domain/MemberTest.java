package com.project.Journey.login.member.domain;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class MemberTest {

    @Test
    void testCreateUser() {
        // Given
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setId("testUser");
        memberDTO.setName("Test Name");
        memberDTO.setPassword("password123");
        memberDTO.setEmail("test@example.com");
        memberDTO.setSocialType("GOOGLE");
        memberDTO.setSocialId("12345");

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        // when
        Member member = Member.createUser(memberDTO, passwordEncoder);

        // Then
        assertEquals("testUser", member.getId());

        assertEquals("Test Name", member.getName());
        assertEquals("test@example.com", member.getEmail());
        assertTrue(passwordEncoder.matches("password123", member.getPassword())); // 비밀번호 암호화 확인
        assertEquals(MemberRole.USER, member.getRole());
        assertEquals(SocialType.GOOGLE, member.getSocialType());
        assertEquals("12345", member.getSocialId());
    }

}