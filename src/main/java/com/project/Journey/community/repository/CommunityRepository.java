package com.project.Journey.community.repository;

import com.project.Journey.board.entity.Post;
import com.project.Journey.community.dto.SearchDTO;
import com.project.Journey.community.entity.Community;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CommunityRepository extends JpaRepository<Community, Long>, JpaSpecificationExecutor<Community> {
    Page<Community> findByCountry(String country, Pageable pageable);
    //Community<Community> findById(Long id);

    // yyyy-mm-dd 형식의 날짜 범위 내에서 게시글 페이징 조회
    /*
    @Query("SELECT c from Community c WHERE c.created_at BETWEEN :startDate AND :endDate")
    Page<Community> findAllByDateRangeAndCountry(
            @Param("startDate")LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            String country,
            Pageable pageable
    );
*/

    // 특정 기간과 국가별 게시글 페이징 조회
    Page<Community> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    //조회수가 높은 커뮤니티 게시글을 지정한 개수만큼 받아오기
    @Query("SELECT c FROM Community c ORDER BY c.viewCount DESC")
    List<Community> findTopCommunityViewCount(Pageable pageable);



}
