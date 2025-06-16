package com.project.Journey.companion.comment.repository;


import com.project.Journey.companion.comment.entity.Comment;
import com.project.Journey.companion.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostAndParentCommentIsNullOrderByCreatedAtAsc(Post post);
    List<Comment> findByParentCommentOrderByCreatedAtAsc(Comment parent);

}