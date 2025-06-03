package com.project.Journey.companion.repository;


import com.project.Journey.companion.entity.Post;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {
    @Transactional
    @Modifying
    @Query("UPDATE Post p SET p.view_count = :increment WHERE p.postId = :postId")
    void incrementViewCount(@Param("postId") Long postId, @Param("increment") int increment);

    @Query("SELECT p FROM Post p WHERE p.member.id = :memberId")
    List<Post> findPostsByMemberId(@Param("memberId") Long memberId);

    //페이지네이션 기능 개발
    Page<Post> findAll(Pageable pageable);

    //나라별 게시글 조회
    Page<Post> findByCountry(String country, Pageable pageable);

    // 특정 기간 + 특정 국가의 게시글 검색 (페이징 적용)
    Page<Post> findByStartDateBetweenAndCountry(LocalDate startDate, LocalDate endDate, String country, Pageable pageable);

    // 지정된 개수만큼 랜덤으로 게시글 조회
    @Query(value = "SELECT * FROM posts ORDER BY RAND() LIMIT :count", nativeQuery = true)
    List<Post> findRandomPosts(@Param("count") int count);

    //동행자 모집 페이지네이션

}