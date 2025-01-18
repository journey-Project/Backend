//package com.project.Journey.login.member.repository;
//
//import com.project.Journey.login.member.domain.Member;
//import com.project.Journey.login.member.domain.MemberRole;
//import com.project.Journey.login.member.domain.SocialType;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.context.annotation.Import;
//import org.springframework.test.annotation.Rollback;
//
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@DataJpaTest
//@Import(MemberRepository.class) // MemberRepository 를 Spring 컨텍스트에 수동으로 등
//class MemberRepositoryTest {
//
//    @Autowired
//    private MemberRepository memberRepository;
//
//    @Test
//    void testSaveAndFindById() {
//        // Given
//        Member member = Member.builder()
//                .id("testUser")
//                .name("Test Name")
//                .password("password123")
//                .email("test@example.com")
//                .role(MemberRole.USER)
//                .socialType(SocialType.GOOGLE)
//                .socialId("12345")
//                .build();
//
//        // when
//        memberRepository.save(member);
//        Optional<Member> foundMember = memberRepository.findById("testUser");
//
//        // Then
//        assertThat(foundMember).isPresent();
//        assertThat(foundMember.get().getId()).isEqualTo("testUser");
//        assertThat(foundMember.get().getEmail()).isEqualTo("test@example.com");
//    }
//
//    @Test
//    void testFindByEmail() {
//        // Given
//        Member member = Member.builder()
//                .id("testUser2")
//                .name("Test Name 2")
//                .password("password456")
//                .email("email@example.com")
//                .role(MemberRole.USER)
//                .socialType(SocialType.KAKAO)
//                .socialId("67890")
//                .build();
//
//        memberRepository.save(member);
//
//        // When
//        Optional<Member> foundMember = memberRepository.findByEmail("email@example.com");
//
//        // Then
//        assertThat(foundMember).isPresent();
//        assertThat(foundMember.get().getEmail()).isEqualTo("email@example.com");
//    }
//
//    @Test
//    void testFindBySocialTypeAndSocialId() {
//        Member member = Member.builder()
//                .id("testUser3")
//                .name("Test Name 3")
//                .password("password789")
//                .email("Last@example.com")
//                .role(MemberRole.USER)
//                .socialType(SocialType.NAVER)
//                .socialId("13579")
//                .build();
//
//        memberRepository.save(member);
//
//        // When
//        Optional<Member> foundMember = memberRepository.findBySocialTypeAndSocialId(SocialType.NAVER, "13579");
//
//        // Then
//        assertThat(foundMember).isPresent();
//        assertThat(foundMember.get().getSocialId()).isEqualTo("13579");
//    }
//}