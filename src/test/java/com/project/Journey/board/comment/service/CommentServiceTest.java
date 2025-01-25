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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private NotificationService notificationService;
    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private CommentService commentService;

    private Post testPost;

    @BeforeEach
    void setUp() {
        // 테스트용 Post 객체
        testPost = Post.builder()
                .postId(1L)
                .user_id("postOwner")
                .title("Test Post")
                .content("Test Content")
                .comment_count(0)
                .build();
    }

    @Test
    @DisplayName("댓글 생성 - 최상위 댓글 성공")
    void testCreateComment_TopLevelComment() {
        // given
        CommentDTO dto = CommentDTO.builder()
                .postId(1L)
                .userId("user1")
                .content("This is a test comment")
                .build();

        // PostRepository mock 설정
        when(postRepository.findById(dto.getPostId())).thenReturn(Optional.of(testPost));

        // CommentRepository mock 설정
        Comment savedComment = Comment.builder()
                .commentId(100L)
                .userId(dto.getUserId())
                .content(dto.getContent())
                .post(testPost)
                .parentComment(null)
                .depth(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);

        // when
        Long commentId = commentService.createComment(dto);

        // then
        assertThat(commentId).isEqualTo(100L);

        // post의 comment_count가 증가하는지 확인
        assertThat(testPost.getComment_count()).isEqualTo(1);

        // Notification 객체가 생성되었는지 확인
        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository, times(1)).save(notificationCaptor.capture());
        Notification capturedNotification = notificationCaptor.getValue();
        assertThat(capturedNotification.getUser_id()).isEqualTo("user1");
        assertThat(capturedNotification.getRecipient()).isEqualTo("postOwner");
        assertThat(capturedNotification.getMessage()).isEqualTo("This is a test comment");

        // NotificationService가 sendNotificationToRecipient를 호출했는지 확인
        ArgumentCaptor<NotificationDTO> notificationDTOCaptor = ArgumentCaptor.forClass(NotificationDTO.class);
        ArgumentCaptor<Notification> notificationEntityCaptor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationService, times(1))
                .sendNotificationToRecipient(notificationDTOCaptor.capture(), notificationEntityCaptor.capture());
    }

    @Test
    @DisplayName("댓글 생성 - 대댓글 성공")
    void testCreateComment_SubComment() {
        // given
        Comment parentComment = Comment.builder()
                .commentId(50L)
                .userId("user2")
                .content("Parent comment")
                .depth(0)
                .post(testPost)
                .build();

        CommentDTO dto = CommentDTO.builder()
                .postId(1L)
                .userId("user3")
                .content("This is a sub comment")
                .parentCommentId(50L)
                .build();

        when(postRepository.findById(dto.getPostId())).thenReturn(Optional.of(testPost));
        when(commentRepository.findById(dto.getParentCommentId())).thenReturn(Optional.of(parentComment));

        Comment subComment = Comment.builder()
                .commentId(60L)
                .userId("user3")
                .content("This is a sub comment")
                .depth(1)
                .parentComment(parentComment)
                .post(testPost)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(commentRepository.save(any(Comment.class))).thenReturn(subComment);

        // when
        Long savedId = commentService.createComment(dto);

        // then
        assertThat(savedId).isEqualTo(60L);
        assertThat(testPost.getComment_count()).isEqualTo(1);
        assertThat(parentComment.getChildComments()).hasSize(1);
        assertThat(parentComment.getChildComments().get(0).getCommentId()).isEqualTo(60L);
    }

    @Test
    @DisplayName("댓글 생성 실패 - 존재하지 않는 게시물 ID")
    void testCreateComment_PostNotFound() {
        // given
        CommentDTO dto = CommentDTO.builder()
                .postId(999L)
                .userId("user1")
                .content("content")
                .build();

        when(postRepository.findById(dto.getPostId())).thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class, () -> commentService.createComment(dto));
    }

    @Test
    @DisplayName("게시글 ID로 모든 댓글 조회")
    void testGetAllCommentsByPostId() {
        // given
        Long postId = 1L;
        Comment parentComment = Comment.builder()
                .commentId(1L)
                .userId("user1")
                .content("Parent comment")
                .depth(0)
                .post(testPost)
                .childComments(new ArrayList<>())
                .build();

        Comment childComment = Comment.builder()
                .commentId(2L)
                .userId("user2")
                .content("Child comment")
                .depth(1)
                .post(testPost)
                .parentComment(parentComment)
                .build();
        parentComment.addChildComment(childComment);

        List<Comment> topComments = new ArrayList<>();
        topComments.add(parentComment);

        when(commentRepository.findByPost_PostIdAndParentCommentIsNull(postId)).thenReturn(topComments);

        // when
        var result = commentService.getAllCommentsByPostId(postId);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCommentId()).isEqualTo(1L);
        assertThat(result.get(0).getChildComments()).hasSize(1);
        assertThat(result.get(0).getChildComments().get(0).getCommentId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("댓글 수정 - 성공")
    void testUpdateComment() {
        // given
        Long commentId = 10L;
        String newContent = "Updated content";

        Comment existingComment = Comment.builder()
                .commentId(commentId)
                .content("Old content")
                .updatedAt(LocalDateTime.now())
                .build();

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(existingComment));

        // when
        commentService.updateComment(commentId, newContent);

        // then
        assertThat(existingComment.getContent()).isEqualTo(newContent);
        verify(commentRepository, times(1)).findById(commentId);
    }

    @Test
    @DisplayName("댓글 수정 실패 - 존재하지 않는 댓글 ID")
    void testUpdateComment_CommentNotFound() {
        // given
        Long commentId = 999L;
        String newContent = "Updated content";

        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class, () ->
                commentService.updateComment(commentId, newContent));
    }

    @Test
    @DisplayName("댓글 삭제 - 성공")
    void testDeleteComment() {
        // given
        Long commentId = 20L;

        Comment existingComment = Comment.builder()
                .commentId(commentId)
                .post(testPost)
                .build();

        testPost.setComment_count(2);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(existingComment));

        // when
        commentService.deleteComment(commentId);

        // then
        verify(commentRepository, times(1)).delete(existingComment);
        assertThat(testPost.getComment_count()).isEqualTo(1);
    }

    @Test
    @DisplayName("댓글 삭제 실패 - 존재하지 않는 댓글 ID")
    void testDeleteComment_CommentNotFound() {
        // given
        Long commentId = 999L;
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class, () -> commentService.deleteComment(commentId));
    }
}
