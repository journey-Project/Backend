package com.project.Journey.community.comment.service;

import com.project.Journey.community.comment.dto.CommunityCommentDTO;
import com.project.Journey.community.comment.dto.CommunityCommentRequest;
import com.project.Journey.community.comment.dto.CommunityCommentResponseDTO;
import com.project.Journey.community.comment.entity.CommunityComment;
import com.project.Journey.community.comment.repository.CommunityCommentRepository;
import com.project.Journey.community.entity.Community;
import com.project.Journey.community.repository.CommunityRepository;
import com.project.Journey.login.member.domain.Member;
import com.project.Journey.login.member.repository.MemberRepository;
import com.project.Journey.notification.entity.NotificationType;
import com.project.Journey.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
@Transactional
public class CommunityCommentService {

    private final CommunityCommentRepository commentRepo;
    private final CommunityRepository        communityRepo;
    private final MemberRepository           memberRepo;
    private final NotificationService notificationService;

    public CommunityCommentResponseDTO createComment(Long communityId,
                                                     CommunityCommentRequest req) {

        Community community = communityRepo.findById(communityId)
                .orElseThrow(() -> new IllegalArgumentException("커뮤니티 글이 없습니다"));
        Member writer = memberRepo.findById(req.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("회원이 없습니다"));

        CommunityComment parent = null;
        if (req.getParentCommentId() != null) {
            parent = commentRepo.findById(req.getParentCommentId())
                    .orElseThrow(() -> new IllegalArgumentException("부모 댓글이 없습니다"));
            if (parent.getParentComment() != null)
                throw new IllegalArgumentException("대댓글은 한 단계만 허용됩니다");
            parent.incrementReplyCount();
        }

        CommunityComment saved = commentRepo.save(
                CommunityComment.builder()
                        .community(community)
                        .writer(writer)
                        .content(req.getContent())
                        .parentComment(parent)
                        .build());
        if (parent == null) {
            Member receiver = community.getWriter();
            if (!writer.getId().equals(receiver.getId())) {
                notificationService.push(
                        receiver,
                        writer,
                        NotificationType.COMMENT,
                        writer.getDisplayName() + "님 댓글을 남겼습니다.",
                        "/community-board/" + community.getCountry() + "/" + community.getCommunityPostId()
                );
            }
        } else {
            Member receiver = community.getWriter();
            if (!writer.getId().equals(receiver.getId())) {
                notificationService.push(
                        receiver,
                        writer,
                        NotificationType.REPLY,
                        writer.getDisplayName() + "님이 대댓글을 남겼습니다.",
                        "/community-board/" + community.getCountry() + "/" + community.getCommunityPostId() + "?commentId=" + parent.getCommentId()
                );
            }
        }

        return CommunityCommentResponseDTO.from(saved);
    }

    @Transactional(readOnly = true)
    public List<CommunityCommentResponseDTO> getRootComments(Long communityId, Long currentMemberId) {

        Community community = communityRepo.findById(communityId)
                .orElseThrow(() -> new IllegalArgumentException("커뮤니티 글이 없습니다"));

        return commentRepo
                .findByCommunityAndParentCommentIsNullAndIsActiveTrueOrderByCreatedAtAsc(community)
                .stream()
                .map(c -> toDtoWithReplies(c, currentMemberId))
                .toList();
    }

    private CommunityCommentResponseDTO toDtoWithReplies(CommunityComment root, Long currentMemberId) {

        boolean rootMine = root.getWriter().getId().equals(currentMemberId);

        List<CommunityCommentResponseDTO> childDtos = commentRepo
                .findByParentCommentAndIsActiveTrueOrderByCreatedAtAsc(root)
                .stream()
                .map(child -> CommunityCommentResponseDTO.of(child,
                        child.getWriter().getId().equals(currentMemberId)))
                .toList();

        return CommunityCommentResponseDTO.of(root, rootMine, childDtos);
    }


    @Transactional(readOnly = true)
    public List<CommunityCommentResponseDTO> getReplies(Long parentId, Long currentMemberId) {

        CommunityComment parent = commentRepo.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 없습니다"));

        return commentRepo
                .findByParentCommentAndIsActiveTrueOrderByCreatedAtAsc(parent)
                .stream()
                .map(child -> CommunityCommentResponseDTO.of(child,
                        child.getWriter().getId().equals(currentMemberId)))
                .toList();
    }

    public CommunityCommentResponseDTO updateComment(Long id, String content) {
        CommunityComment c = commentRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 없습니다"));
        if (!c.isActive())
            throw new IllegalStateException("삭제된 댓글은 수정 불가");

        c.updateContent(content);
        return CommunityCommentResponseDTO.from(c);
    }

    public void deleteComment(Long id) {
        CommunityComment c = commentRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 없습니다"));

        if (!c.isActive()) return;           // 이미 삭제
        c.deactivate();

        if (c.getParentComment() != null)
            c.getParentComment().decrementReplyCount();
    }
}
