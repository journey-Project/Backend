//package com.project.Journey.login.member.service;
//
//import com.project.Journey.login.member.domain.Member;
//import com.project.Journey.login.member.dto.MemberDTO;
//import com.project.Journey.login.member.domain.SocialType;
//import com.project.Journey.login.member.repository.MemberRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.*;
//
//class MemberServiceTest {
//
//    @Mock
//    private MemberRepository memberRepository;
//
//    @Mock
//    private PasswordEncoder passwordEncoder;
//
//    @InjectMocks
//    private MemberService memberService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    @DisplayName("유효한 회원을 저장하고 저장된 엔티티 반환")
//    void testSave() {
//        MemberDTO dto = new MemberDTO();
//        dto.setId("testUser");
//        dto.setName("Test User");
//        dto.setPassword("password");
//        dto.setEmail("test@example.com");
//        dto.setSocialType("GOOGLE");
//        dto.setSocialId("12345");
//
//        Member member = Member.createUser(dto, passwordEncoder);
//        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
//        when(memberRepository.save(any(Member.class))).thenReturn(member);
//
//        Member savedMember = memberService.save(dto);
//
//        assertNotNull(savedMember);
//        assertEquals("testUser", savedMember.getId());
//        verify(memberRepository, times(1)).save(any(Member.class));
//
//    }
//
//    @Test
//    @DisplayName("ID로 회원을 조회할 때 회원이 존재하는 경우")
//    void testFindById() {
//        Member member = new Member();
//        member.setId("testUser");
//
//        when(memberRepository.findById("testUser")).thenReturn(Optional.of(member));
//
//        Optional<Member> result = memberService.findById("testUser");
//
//        assertTrue(result.isPresent());
//        assertEquals("testUser", result.get().getId());
//        verify(memberRepository, times(1)).findById("testUser");
//
//    }
//
//    @Test
//    @DisplayName("이메일로 회원을 조회할 때 이메일이 존재하는 경우")
//    void testFindByEmail() {
//        Member member = new Member();
//        member.setEmail("test@example.com");
//
//        when(memberRepository.findByEmail("test@example.com")).thenReturn(Optional.of(member));
//
//        Optional<Member> result = memberService.findByEmail("test@example.com");
//
//        assertTrue(result.isPresent());
//        assertEquals("test@example.com", result.get().getEmail());
//        verify(memberRepository, times(1)).findByEmail("test@example.com");
//    }
//
//    @Test
//    @DisplayName("소셜 타입과 소셜 ID로 회원을 조회할 때")
//    void testFindBySocialTypeAndSocialId() {
//        Member member = new Member();
//        member.setSocialType(SocialType.GOOGLE);
//        member.setSocialId("123");
//
//        when(memberRepository.findBySocialTypeAndSocialId(SocialType.GOOGLE, "123"))
//                .thenReturn(Optional.of(member));
//
//        Optional<Member> result = memberService.findBySocialTypeAndSocialId(SocialType.GOOGLE, "123");
//
//
//        assertTrue(result.isPresent());
//        assertEquals(SocialType.GOOGLE, result.get().getSocialType());
//        assertEquals("123", result.get().getSocialId());
//        verify(memberRepository, times(1)).findBySocialTypeAndSocialId(SocialType.GOOGLE, "123");
//    }
//}