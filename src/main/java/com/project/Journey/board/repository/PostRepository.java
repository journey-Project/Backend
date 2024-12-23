package com.project.Journey.board.repository;


import com.project.Journey.board.entity.Post;
import jakarta.transaction.Transactional;
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
    @Query("UPDATE Post p SET p.view_count = :increment WHERE p.post_id = :post_id")
    void incrementViewCount(@Param("post_id") Long postId, @Param("increment") int increment);



}