package com.project.Journey.story.repository;

import com.project.Journey.login.member.domain.Member;
import com.project.Journey.story.entity.Story;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface StoryRepository extends JpaRepository<Story, Long> {

    // 기존: 전체 활성 스토리
    @Query("SELECT s FROM Story s WHERE s.author IN :authors AND s.expireAt > :now ORDER BY s.createdAt DESC")
    List<Story> findActiveByAuthors(
            @Param("authors") List<Member> authors,
            @Param("now") LocalDateTime now
    );

    // 변경: pageable 지원(최신순으로, 최대 limit)
    List<Story> findByAuthorInAndExpireAtGreaterThanOrderByCreatedAtDesc(
            List<Member> authors,
            LocalDateTime now,
            Pageable pageable
    );

    List<Story> findByAuthorOrderByCreatedAtDesc(Member author);


    //내가 마지막으로 올린(최근에 올린) 스토리 조회
    Optional<Story> findTop1ByAuthor_LoginIdOrderByCreatedAtDesc(@Param("loginId") String loginId);


}
