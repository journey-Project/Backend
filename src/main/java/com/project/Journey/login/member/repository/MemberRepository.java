package com.project.Journey.login.member.repository;

import com.project.Journey.login.member.domain.Member;
import com.project.Journey.login.member.domain.SocialType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class MemberRepository {
    private final EntityManager em;

    public MemberRepository(EntityManager em) {
        this.em = em;
    }

    public Member save(Member member){
        em.persist(member);
        return member;
    }

    public Optional<Member> findById(String id){
        String jpql = "select u from Member u where u.id=:id";
        TypedQuery<Member> query = em.createQuery(jpql, Member.class).setParameter("id", id);
        List<Member> MemberList = query.getResultList();
        if (MemberList.size() == 0) {
            return Optional.empty();
        }
        return Optional.ofNullable(MemberList.get(0));
    }

    public Optional<Member> findByEmail(String email) {
        String jpql = "select u from Member u where u.email=:email";
        TypedQuery<Member> query = em.createQuery(jpql, Member.class).setParameter("email", email);
        List<Member> MemberList = query.getResultList();
        if(MemberList.size() == 0){
            return Optional.empty();
        }
        return Optional.ofNullable(MemberList.get(0));
    }

    public Optional<Member> findBySocialTypeAndSocialId(SocialType socialType, String socialId) {
        String jpql = "select u from Member u where u.socialType=:socialType and u.socialId=:socialId";
        TypedQuery<Member> query = em.createQuery(jpql, Member.class)
                .setParameter("socialType", socialType)
                .setParameter("socialId", socialId);
        List<Member> MemberList = query.getResultList();
        if (MemberList.size() == 0) {
            return Optional.empty();
        }
        return Optional.ofNullable(MemberList.get(0));
    }

}
