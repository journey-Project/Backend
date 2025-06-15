package com.project.Journey.companion.comment.service;


import com.project.Journey.companion.comment.dto.CommentResponseDTO;
import com.project.Journey.companion.comment.entity.Comment;
import com.project.Journey.companion.comment.repository.CommentRepository;

import com.project.Journey.companion.entity.Post;

import com.project.Journey.companion.repository.PostRepository;
import com.project.Journey.login.member.domain.Member;
import com.project.Journey.login.member.repository.MemberRepository;
import com.project.Journey.notification.entity.NotificationType;
import com.project.Journey.notification.service.NotificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final NotificationService notificationService;
    public Comment createComment(Long postId, Long memberId,
                                 String content, Long parentCommentId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다: " + postId));

        Member writer = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다: " + memberId));

        Comment parent = null;
        if (parentCommentId != null) {
            parent = commentRepository.findById(parentCommentId)
                    .orElseThrow(() -> new IllegalArgumentException("부모 댓글이 존재하지 않습니다: " + parentCommentId));
            if (parent.getParentComment() != null)
                throw new IllegalArgumentException("대댓글은 한 단계만 허용됩니다");
            parent.incrementReplyCount();
        }

        Comment saved = commentRepository.save(
                Comment.builder()
                        .post(post)
                        .writer(writer)
                        .content(content)
                        .parentComment(parent)
                        .build());

        if (parent == null) {
            Member receiver = post.getWriter();
            if (!writer.getId().equals(receiver.getId())) {
                notificationService.push(
                        receiver,
                        writer,
                        NotificationType.COMMENT,
                        writer.getDisplayName() + "님이 댓글을 남겼습니다.",
                        "/companion-board/" + post.getCountry() + "/" + post.getId()
                );
            }
        } else {
            Member receiver = parent.getWriter();
            if (!writer.getId().equals(receiver.getId())) {
                notificationService.push(
                        receiver,
                        writer,
                        NotificationType.REPLY,
                        writer.getDisplayName() + "님이 대댓글을 남겼습니다.",
                        "/companion-board/" + post.getCountry() + "/" + post.getId() + "?commentId=" + parent.getCommentId()
                );
            }
        }


        return saved;
    }

    public List<CommentResponseDTO> getCommentsByPost(Long postId, Long currentMemberId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 없음: "+ postId));

        return commentRepository
                .findByPostAndParentCommentIsNullAndIsActiveTrueOrderByCreatedAtAsc(post)
                .stream()
                .map(c -> toDtoWithChildren(c, currentMemberId))
                .toList();
    }

    private CommentResponseDTO toDtoWithChildren(Comment root, Long currentId) {

        boolean mineRoot = currentId != null && root.getWriter().getId().equals(currentId);

        List<CommentResponseDTO> childDtos = commentRepository
                .findByParentCommentAndIsActiveTrueOrderByCreatedAtAsc(root)
                .stream()
                .map(child -> CommentResponseDTO.of(
                        child,
                        currentId != null && child.getWriter().getId().equals(currentId)))
                .toList();

        return CommentResponseDTO.of(root, mineRoot, childDtos);   // ← replies 포함
    }

    public List<Comment> getReplies(Long parentCommentId) {
        Comment parent = commentRepository.findById(parentCommentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다: " + parentCommentId));

        return commentRepository
                .findByParentCommentAndIsActiveTrueOrderByCreatedAtAsc(parent);
    }


    public Comment updateComment(Long commentId, String content, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다: " + commentId));

        if (!comment.getWriter().getId().equals(userId)) {
            throw new IllegalStateException("수정 권한이 없습니다");
        }
        if (!comment.isActive()) {
            throw new IllegalStateException("삭제된 댓글은 수정할 수 없습니다");
        }
        comment.setContent(content);
        return comment;
    }

    public void deleteComment(Long commentId) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다: " + commentId));

        if (!comment.isActive()) return;          // 이미 삭제된 경우 무시

        comment.deactivate();                     // 물리 삭제 대신 플래그만 변경

        if (comment.getParentComment() != null) {
            comment.getParentComment().decrementReplyCount();
        }
    }
}