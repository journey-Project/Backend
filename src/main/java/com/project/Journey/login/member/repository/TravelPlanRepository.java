package com.project.Journey.login.member.repository;

import com.project.Journey.login.member.domain.Member;
import com.project.Journey.login.member.domain.TravelPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TravelPlanRepository extends JpaRepository<TravelPlan, Long> {
    List<TravelPlan> findByMemberOrderByStartDateAsc(Member member);
}
