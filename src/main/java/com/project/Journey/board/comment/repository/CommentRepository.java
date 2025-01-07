package com.project.Journey.board.comment.repository;


import com.project.Journey.board.comment.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // (1) 특정 게시글 + 부모가 없는(최상위) 댓글 조회
    List<Comment> findByPost_PostIdAndParentCommentIsNull(Long postId);

    // 필요하다면, 특정 부모 댓글의 자식 조회
    List<Comment> findByParentComment_CommentId(Long parentId);
}
