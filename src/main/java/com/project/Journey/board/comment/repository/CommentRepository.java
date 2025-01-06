package com.project.Journey.board.comment.repository;


import com.project.Journey.board.comment.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 특정 post_id에 대한 댓글 목록 조회
    List<Comment> findByPost_PostId(Long postId);

}
