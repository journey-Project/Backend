package com.project.Journey.board.comment.repository;


import com.project.Journey.board.comment.entity.Comment;
import com.project.Journey.board.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostAndParentCommentIsNullAndIsActiveTrueOrderByCreatedAtAsc(Post post);
    List<Comment> findByParentCommentAndIsActiveTrueOrderByCreatedAtAsc(Comment parent);
}