package com.project.Journey.login.member.repository;

import com.project.Journey.login.member.domain.Member;
import com.project.Journey.login.member.domain.MemberTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberTagRepository extends JpaRepository<MemberTag, Long> {
    List<MemberTag> findByMember(Member member);
    void deleteByMember(Member member);
}
