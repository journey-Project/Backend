//package com.project.Journey.login.member.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.project.Journey.login.member.domain.Member;
//import com.project.Journey.login.member.domain.MemberDTO;
//import com.project.Journey.login.member.service.MemberService;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.Optional;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(MemberController.class)
//class MemberControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private MemberService memberService;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Test
//    @DisplayName("회원 가입 성공")
//    @WithMockUser // 인증된 사용자로 요청 수행
//    void testSignUpSuccess() throws Exception {
//        // Given
//        MemberDTO memberDTO = new MemberDTO();
//        memberDTO.setId("testUser");
//        memberDTO.setName("Test Name");
//        memberDTO.setPassword("password123");
//        memberDTO.setEmail("test@example.com");
//
//        when(memberService.save(any(MemberDTO.class))).thenReturn(new Member());
//
//        // When & Then
//        mockMvc.perform(post("/api/members/signUp")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(memberDTO))
//                        .with(csrf())) // CSRF 토큰 추가
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    @DisplayName("중복된 ID로 회원가입 실패")
//    @WithMockUser
//    void testDuplicateId() throws Exception {
//        MemberDTO memberDTO = new MemberDTO();
//        memberDTO.setId("duplicateUser");
//        memberDTO.setName("Test Name");
//        memberDTO.setPassword("password123");
//        memberDTO.setEmail("test@example.com");
//
//        when(memberService.findById("duplicateUser")).thenReturn(Optional.of(new Member()));
//
//        mockMvc.perform(post("/api/members/signUp")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(memberDTO))
//                        .with(csrf()))
//                .andExpect(status().isConflict())
//                .andExpect(content().string("이미 존재하는 아이디입니다"));
//    }
//
//    @Test
//    @DisplayName("중복된 이메일로 회원가입 실패")
//    @WithMockUser
//    void testDuplcateEmail() throws Exception {
//        MemberDTO memberDTO = new MemberDTO();
//        memberDTO.setId("uniqueUser");
//        memberDTO.setName("Test Name");
//        memberDTO.setPassword("password123");
//        memberDTO.setEmail("duplicate@example.com");
//
//        when(memberService.findById("uniqueUser")).thenReturn(Optional.empty());
//        when(memberService.findByEmail("duplicate@example.com")).thenReturn(Optional.of(new Member()));
//
//        mockMvc.perform(post("/api/members/signUp")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(memberDTO))
//                        .with(csrf()))
//                .andExpect(status().isConflict())
//                .andExpect(content().string("이미 존재하는 이메일입니다"));
//    }
//
//    @Test
//    @DisplayName("유효성 검증 실패 - 정보 누락")
//    @WithMockUser
//    void testValitationFailure() throws Exception {
//        MemberDTO memberDTO = new MemberDTO();
//
//        mockMvc.perform(post("/api/members/signUp")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(memberDTO))
//                        .with(csrf()))
//                .andExpect(status().isBadRequest())
//
//                .andExpect(jsonPath("$.error-id").value("ID는 필수 값입니다"))
//                .andExpect(jsonPath("$.error-name").value("이름은 필수 값입니다"))
//                .andExpect(jsonPath("$.error-password").value("비밀번호는 필수 값입니다"))
//                .andExpect(jsonPath("$.error-email").value("이메일은 필수 값입니다"));
//    }
//
//    @Test
//    @DisplayName("유효성 검증 실패 - 잘못된 이메일 형식인 경우")
//    @WithMockUser
//    void testInvalidEmailFormat() throws Exception {
//        MemberDTO invalidEmailDTO = new MemberDTO();
//        invalidEmailDTO.setId("testUser");
//        invalidEmailDTO.setName("Test Name");
//        invalidEmailDTO.setPassword("password123");
//        invalidEmailDTO.setEmail("invalid-email"); // 이메일 형식이 잘못됨
//
//        mockMvc.perform(post("/api/members/signUp")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(invalidEmailDTO))
//                        .with(csrf()))
//                .andExpect(status().isBadRequest()) // 400 상태 코드 확인
//                .andExpect(jsonPath("$.error-email").value("올바른 이메일 형식이어야 합니다"));
//    }
//}