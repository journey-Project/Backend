package com.project.Journey.login.member.repository;

import com.project.Journey.login.member.domain.Member;
import com.project.Journey.login.member.domain.SocialType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByLoginId(String loginId);
    Optional<Member> findByEmail(String email);
    Optional<Member> findBySocialTypeAndSocialId(SocialType socialType, String socialId);
    List<Member> findAllBySocialTypeAndSocialId(SocialType socialType, String socialId);
}
