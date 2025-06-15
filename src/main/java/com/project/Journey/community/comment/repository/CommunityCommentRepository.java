package com.project.Journey.community.comment.repository;

import com.project.Journey.community.comment.entity.CommunityComment;
import com.project.Journey.community.entity.Community;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommunityCommentRepository extends JpaRepository<CommunityComment, Long> {
    List<CommunityComment> findByCommunityAndParentCommentIsNullOrderByCreatedAtAsc(Community community);
    List<CommunityComment> findByParentCommentAndIsActiveTrueOrderByCreatedAtAsc(CommunityComment parent);
    List<CommunityComment> findByParentComment_CommentIdAndIsActiveTrueOrderByCreatedAtAsc(Long parentId);

}
