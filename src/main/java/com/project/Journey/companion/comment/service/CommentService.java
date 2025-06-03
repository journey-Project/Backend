package com.project.Journey.companion.comment.service;


import com.project.Journey.companion.comment.entity.Comment;
import com.project.Journey.companion.comment.repository.CommentRepository;

import com.project.Journey.companion.entity.Post;

import com.project.Journey.companion.repository.PostRepository;
import com.project.Journey.login.member.domain.Member;
import com.project.Journey.login.member.repository.MemberRepository;
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

    public Comment createComment(Long postId, Long memberId,
                                 String content, Long parentCommentId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다: " + postId));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다: " + memberId));

        /* 부모 댓글 검증 / replyCount 증감 로직은 그대로 */
        Comment parent = null;
        if (parentCommentId != null) {
            parent = commentRepository.findById(parentCommentId)
                    .orElseThrow(() -> new IllegalArgumentException("부모 댓글이 존재하지 않습니다: " + parentCommentId));
            if (parent.getParentComment() != null)
                throw new IllegalArgumentException("대댓글은 한 단계만 허용됩니다");
            parent.incrementReplyCount();
        }

        Comment comment = Comment.builder()
                .post(post)
                .member(member)          // ⭐ FK 세팅
                .content(content)
                .parentComment(parent)
                .build();

        return commentRepository.save(comment);
    }

    /* ---------- 댓글 목록 ---------- */
    public List<Comment> getCommentsByPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다: " + postId));

        return commentRepository
                .findByPostAndParentCommentIsNullAndIsActiveTrueOrderByCreatedAtAsc(post);
    }

    public List<Comment> getReplies(Long parentCommentId) {
        Comment parent = commentRepository.findById(parentCommentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다: " + parentCommentId));

        return commentRepository
                .findByParentCommentAndIsActiveTrueOrderByCreatedAtAsc(parent);
    }

    /* ---------- 댓글 수정 ---------- */
    public Comment updateComment(Long commentId, String content) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다: " + commentId));

        if (!comment.isActive()) {
            throw new IllegalStateException("삭제된 댓글은 수정할 수 없습니다");
        }
        comment.setContent(content);
        return comment;
    }

    /* ---------- 논리 삭제 ---------- */
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