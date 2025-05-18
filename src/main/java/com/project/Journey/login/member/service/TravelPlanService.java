package com.project.Journey.login.member.service;

import com.project.Journey.login.member.domain.Member;
import com.project.Journey.login.member.domain.TravelPlan;
import com.project.Journey.login.member.dto.TravelPlanRequest;
import com.project.Journey.login.member.dto.TravelPlanResponse;
import com.project.Journey.login.member.repository.MemberRepository;
import com.project.Journey.login.member.repository.TravelPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service @RequiredArgsConstructor
@Transactional
public class TravelPlanService {

    private final MemberRepository memberRepo;
    private final TravelPlanRepository planRepo;

    public TravelPlanResponse addPlan(Long memberId, TravelPlanRequest req) {
        Member m = memberRepo.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원 없음"));

        TravelPlan saved = planRepo.save(
                new TravelPlan(null,
                        m,
                        req.getTitle(),
                        req.getCountry(),
                        req.getCity(),
                        req.getStartDate(),
                        req.getEndDate())
        );

        return TravelPlanResponse.of(saved);
    }

    @Transactional(readOnly = true)
    public List<TravelPlanResponse> listPlans(Long memberId) {
        Member m = memberRepo.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원 없음"));

        return planRepo.findByMemberOrderByStartDateAsc(m)
                .stream().map(TravelPlanResponse::of).toList();

    }

    public TravelPlanResponse updatePlan(Long planId, TravelPlanRequest req) {
        TravelPlan tp = planRepo.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("일정 없음"));

        tp.update(req.getTitle(),
                req.getCountry(),
                req.getCity(),
                req.getStartDate(),
                req.getEndDate());

        return TravelPlanResponse.of(tp);
    }

    public void deletePlan(Long planId) {
        planRepo.deleteById(planId);
    }


}

