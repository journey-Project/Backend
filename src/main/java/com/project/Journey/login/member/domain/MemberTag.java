package com.project.Journey.login.member.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"member_id", "tag"}))
public class MemberTag {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(length = 6, nullable = false)
    private String tag;

    public MemberTag(Member member, String tag) {
        this.member = member;
        this.tag = tag;
    }
}
