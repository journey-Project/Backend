package com.project.Journey.login.member.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TravelPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String title;
    private String country;
    private String city;
    private LocalDate startDate;
    private LocalDate endDate;

    public void update(String title,
                       String country,
                       String city,
                       LocalDate startDate,
                       LocalDate endDate) {
        this.title = title;
        this.country = country;
        this.city = city;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
