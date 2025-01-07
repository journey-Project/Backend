package com.project.Journey.board.comment.service;


import com.project.Journey.board.comment.domain.Comment;
import com.project.Journey.board.comment.domain.CommentDTO;
import com.project.Journey.board.comment.repository.CommentRepository;
import com.project.Journey.board.entity.Post;
import com.project.Journey.board.repository.PostRepository;
import com.project.Journey.notification.dto.NotificationDTO;
import com.project.Journey.notification.entity.Notification;
import com.project.Journey.notification.repository.NotificationRepository;
import com.project.Journey.notification.service.NotificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;
    /**
     * 댓글(대댓글) 생성
     * - parentCommentId가 null이면 최상위 댓글, 아니면 대댓글.
     */
    @Transactional
    public Long createComment(CommentDTO dto) {
        // 1) 게시글 찾기
        Post post = postRepository.findById(dto.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("해당 postId의 게시글이 없습니다"));

        // 2) 부모 댓글 찾기 (대댓글일 경우)
        Comment parent = null;
        int depthLevel = 0;
        if (dto.getParentCommentId() != null) {
            parent = commentRepository.findById(dto.getParentCommentId())
                    .orElseThrow(() -> new IllegalArgumentException("해당 parentCommentId의 댓글이 없습니다"));
            depthLevel = parent.getDepth() + 1; // 부모의 depth +1
        }

        // 3) 새 댓글 엔티티 생성
        Comment comment = Comment.builder()
                .userId(dto.getUserId())
                .content(dto.getContent())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .post(post)
                .parentComment(parent)
                .depth(depthLevel)
                .build();

        Comment savedComment = commentRepository.save(comment);

        // 4) 게시글 commentCount 증가 (원한다면)
        post.setComment_count(post.getComment_count() + 1);

        // 5) 부모가 있다면 양방향 연관관계 설정
        if (parent != null) {
            parent.addChildComment(savedComment);
        }

        //댓글 알림

        Notification notification = new Notification();
        notification.setUser_id(dto.getUserId());
        notification.setRecipient(post.getUser_id());
        notification.setPost_id(dto.getPostId());
        notification.setMessage(dto.getContent());
        notification.setCreated_at(LocalDateTime.now());
        notification.set_read(false);

        NotificationDTO notificationDTO = NotificationDTO.fromEntity(notification);
        Notification savedNotification = notificationRepository.save(notification);


        notificationService.sendNotificationToRecipient(notificationDTO, savedNotification);


        return savedComment.getCommentId();
    }

    /**
     * 게시글의 모든 댓글(대댓글 포함) 조회
     * - 최상위 댓글만 조회 → 각 댓글의 자식 목록(childComments)을 재귀적으로 DTO 변환
     */
    public List<CommentDTO> getAllCommentsByPostId(Long postId) {
        // 1) 최상위 댓글들 (parentComment = null)
        List<Comment> topComments = commentRepository.findByPost_PostIdAndParentCommentIsNull(postId);

        // 2) 재귀적으로 childComments까지 DTO로 변환
        List<CommentDTO> result = new ArrayList<>();
        for (Comment comment : topComments) {
            CommentDTO dto = convertToDTOWithChildren(comment);
            result.add(dto);
        }
        return result;
    }

    // 자식 댓글까지 재귀 변환
    private CommentDTO convertToDTOWithChildren(Comment comment) {
        CommentDTO dto = CommentDTO.builder()
                .commentId(comment.getCommentId())
                .userId(comment.getUserId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .postId(comment.getPost().getPostId())
                .depth(comment.getDepth())
                .parentCommentId(comment.getParentComment() == null
                        ? null : comment.getParentComment().getCommentId())
                .build();

        // childComments
        List<CommentDTO> childDtos = new ArrayList<>();
        for (Comment child : comment.getChildComments()) {
            childDtos.add(convertToDTOWithChildren(child));
        }
        dto.setChildComments(childDtos);

        return dto;
    }

    /**
     * 댓글 수정
     */
    @Transactional
    public void updateComment(Long commentId, String newContent) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 commentId의 댓글이 없습니다"));
        comment.updateComment(newContent);
    }

    /**
     * 댓글 삭제
     * (대댓글 포함 삭제 시에도 cascade + orphanRemoval = true라면 자식도 자동 삭제)
     */
    @Transactional
    public void deleteComment(Long commentId) {
        // 1) 댓글 찾기
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 commentId의 댓글이 없습니다"));

        // 2) 연관된 게시글
        Post post = comment.getPost();
        // 3) 댓글 삭제
        commentRepository.delete(comment);

        // 4) 게시글의 commentCount 감소 (필요하면)
        if (post.getComment_count() > 0) {
            post.setComment_count(post.getComment_count() - 1);
        }
    }
}
