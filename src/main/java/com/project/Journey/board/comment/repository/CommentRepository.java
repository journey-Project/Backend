package com.project.Journey.board.comment.repository;


import com.project.Journey.board.comment.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // (1) 특정 게시글 + 부모가 없는(최상위) 댓글 조회
    List<Comment> findByPost_PostIdAndParentCommentIsNull(Long postId);

    // 필요하다면, 특정 부모 댓글의 자식 조회
    List<Comment> findByParentComment_CommentId(Long parentId);

    // 추가한 메서드(N+1 쿼리 문제 해결)
    @Query("SELECT c FROM Comment c LEFT JOIN FETCH c.childComments WHERE c.post.postId = :postId")
    List<Comment> findAllCommentsWithChildrenByPostId(Long postId);
}
