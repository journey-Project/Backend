package com.project.Journey.board.repository;


import com.project.Journey.board.entity.Post;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    @Transactional
    @Modifying
    @Query("UPDATE Post p SET p.view_count = :increment WHERE p.postId = :postId")
    void incrementViewCount(@Param("postId") Long postId, @Param("increment") int increment);

    @Query("SELECT p FROM Post p WHERE p.user_id = :user_id")
    List<Post> findPostsByUserId(@Param("user_id") String user_id);

    //페이지네이션 기능 개발
    Page<Post> findAll(Pageable pageable);

}