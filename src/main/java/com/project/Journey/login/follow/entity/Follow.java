package com.project.Journey.login.follow.entity;

import com.project.Journey.login.member.domain.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"follower_id", "following_id"}))
@Setter
@Getter
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id")
    private Member follower; //나 : 팔로우를 요청한 사람

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "following_id")
    private Member following; //상대 : 팔로우 당한 사람

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
