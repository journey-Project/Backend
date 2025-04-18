package com.project.Journey.board.comment.service;


import com.project.Journey.board.comment.entity.Comment;
import com.project.Journey.board.comment.repository.CommentRepository;

import com.project.Journey.board.entity.Post;

import com.project.Journey.board.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public Comment createComment(Long postId, String userId, String content, Long parentCommentId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다: " + postId));

        Comment parent = null;
        if (parentCommentId != null) {
            parent = commentRepository.findById(parentCommentId)
                    .orElseThrow(() -> new IllegalArgumentException("부모 댓글이 존재하지 않습니다: " + parentCommentId));
        }

        Comment comment = Comment.builder()
                .post(post)
                .userId(userId)
                .content(content)
                .parentComment(parent)
                .build();

        return commentRepository.save(comment);
    }

    public List<Comment> getCommentsByPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다: " + postId));

        List<Comment> rootComments = commentRepository.findByPostAndParentCommentIsNullOrderByCreatedAtAsc(post);
        return rootComments;
    }

    public List<Comment> getReplies(Long parentCommentId) {
        Comment parent = commentRepository.findById(parentCommentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다: " + parentCommentId));

        return commentRepository.findByParentCommentOrderByCreatedAtAsc(parent);
    }

    public Comment updateComment(Long commentId, String content) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다: " + commentId));
        comment.setContent(content);
        return commentRepository.save(comment);
    }

    public void deleteComment(Long commentId) {
        // 대댓글이 있는 경우 어떻게 처리할지 결정 필요 (함께 삭제 vs 불가)
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다: " + commentId));
        commentRepository.delete(comment);
    }
}