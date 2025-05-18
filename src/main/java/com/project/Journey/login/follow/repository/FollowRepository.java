package com.project.Journey.login.follow.repository;

import com.project.Journey.login.follow.entity.Follow;
import com.project.Journey.login.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    boolean existsByFollowerAndFollowing(Member follower, Member following);
    Optional<Follow> findByFollowerAndFollowing(Member follower, Member following);

    //내가 팔로우한 사람들
    List<Follow> findByFollower(Member follower);

    //나를 팔로우한 사람들
    List<Follow> findByFollowing(Member following);
}
