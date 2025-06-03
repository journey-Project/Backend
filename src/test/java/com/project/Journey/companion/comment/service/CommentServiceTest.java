//package com.project.Journey.board.comment.service;
//
//import com.project.Journey.board.comment.domain.Comment;
//import com.project.Journey.board.comment.domain.CommentDTO;
//import com.project.Journey.board.comment.repository.CommentRepository;
//import com.project.Journey.board.comment.service.CommentService;
//import com.project.Journey.board.entity.Post;
//import com.project.Journey.board.repository.PostRepository;
//import com.project.Journey.notification.dto.NotificationDTO;
//import com.project.Journey.notification.entity.Notification;
//import com.project.Journey.notification.repository.NotificationRepository;
//import com.project.Journey.notification.service.NotificationService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.*;
//import org.springframework.dao.EmptyResultDataAccessException;
//
//import java.time.LocalDateTime;
//import java.util.*;
//
//import static org.assertj.core.api.Assertions.*;
//import static org.mockito.BDDMockito.*;
//
//@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
//class CommentServiceTest {
//
//    @Mock
//    private CommentRepository commentRepository;
//
//    @Mock
//    private PostRepository postRepository;
//
//    @Mock
//    private NotificationService notificationService;
//
//    @Mock
//    private NotificationRepository notificationRepository;
//
//    @InjectMocks
//    private CommentService commentService;
//
//    private Post mockPost;
//
//    @BeforeEach
//    void setUp() {
//        // 예시용: 테스트에서 사용할 가짜 Post 객체
//        mockPost = new Post();
//        mockPost.setPostId(100L);
//        mockPost.setUser_id("postOwner");
//        mockPost.setComment_count(0);
//    }
//
//    @Test
//    void testCreateComment_noParent() {
//        // given
//        CommentDTO dto = CommentDTO.builder()
//                .userId("tester")
//                .content("This is a new comment")
//                .postId(100L)
//                .build();
//
//        given(postRepository.findById(100L)).willReturn(Optional.of(mockPost));
//
//        // commentRepository.save(...) 시나리오 가정
//        Comment savedComment = Comment.builder()
//                .commentId(1L)
//                .userId("tester")
//                .content("This is a new comment")
//                .post(mockPost)
//                .depth(0)
//                .createdAt(LocalDateTime.now())
//                .updatedAt(LocalDateTime.now())
//                .build();
//        given(commentRepository.save(any(Comment.class))).willReturn(savedComment);
//
//        // notificationRepository
//        given(notificationRepository.save(any(Notification.class))).willAnswer(invocation -> {
//            Notification n = invocation.getArgument(0);
//            n.setPost_id(999L); // mock id
//            return n;
//        });
//
//        // when
//        Long createdId = commentService.createComment(dto);
//
//        // then
//        assertThat(createdId).isEqualTo(1L);  // save(...) 결과 commentId=1L 이라고 가정
//
//        // post.comment_count == 1
//        assertThat(mockPost.getComment_count()).isEqualTo(1);
//
//        // verify: postRepository, commentRepository 호출 여부
//        verify(postRepository).findById(100L);
//        verify(commentRepository).save(any(Comment.class));
//        // 알림 관련
//        verify(notificationRepository).save(any(Notification.class));
//        verify(notificationService).sendNotificationToRecipient(any(NotificationDTO.class), any(Notification.class));
//    }
//
//    @Test
//    void testCreateComment_withParent() {
//        // given
//        CommentDTO dto = CommentDTO.builder()
//                .userId("tester")
//                .content("Child comment")
//                .postId(100L)
//                .parentCommentId(10L) // 부모 댓글
//                .build();
//
//        given(postRepository.findById(100L)).willReturn(Optional.of(mockPost));
//
//        Comment parentComment = Comment.builder()
//                .commentId(10L)
//                .userId("parentUser")
//                .content("Parent content")
//                .depth(0)
//                .post(mockPost)
//                .build();
//
//        given(commentRepository.findById(10L)).willReturn(Optional.of(parentComment));
//
//        Comment savedComment = Comment.builder()
//                .commentId(11L)
//                .userId("tester")
//                .content("Child comment")
//                .depth(1) // parent.depth + 1
//                .post(mockPost)
//                .parentComment(parentComment)
//                .build();
//
//        given(commentRepository.save(any(Comment.class))).willReturn(savedComment);
//        given(notificationRepository.save(any(Notification.class))).willReturn(new Notification());
//
//        // when
//        Long newId = commentService.createComment(dto);
//
//        // then
//        assertThat(newId).isEqualTo(11L);
//        assertThat(mockPost.getComment_count()).isEqualTo(1);
//
//        verify(commentRepository).findById(10L);
//        verify(commentRepository).save(any(Comment.class));
//        // 부모 댓글에 childComment가 추가됐는지 확인
//        assertThat(parentComment.getChildComments()).hasSize(1);
//    }
//
//    @Test
//    void testGetAllCommentsByPostId() {
//        // given
//        Long postId = 100L;
//
//        // Comment A (parent)
//        Comment commentA = Comment.builder()
//                .commentId(1L)
//                .userId("userA")
//                .content("first comment")
//                .depth(0)
//                .post(mockPost) // ★ post 설정
//                .createdAt(LocalDateTime.now())
//                .updatedAt(LocalDateTime.now())
//                .build();
//
//        // Comment B (child of A)
//        Comment commentB = Comment.builder()
//                .commentId(2L)
//                .userId("userB")
//                .content("child comment")
//                .depth(1)
//                .parentComment(commentA)
//                .post(mockPost) // ★ post 설정
//                .createdAt(LocalDateTime.now())
//                .updatedAt(LocalDateTime.now())
//                .build();
//
//        // A의 자식 목록에 B를 추가
//        commentA.addChildComment(commentB);
//
//        List<Comment> mockComments = Arrays.asList(commentA, commentB);
//
//        given(commentRepository.findAllCommentsWithChildrenByPostId(postId))
//                .willReturn(mockComments);
//
//        // when
//        List<CommentDTO> result = commentService.getAllCommentsByPostId(postId);
//
//        // then
//        assertThat(result).hasSize(1); // 최상위 댓글은 하나만 (A)
//        CommentDTO parentDto = result.get(0);
//        assertThat(parentDto.getCommentId()).isEqualTo(1L);
//        assertThat(parentDto.getChildComments()).hasSize(1); // 자식 1개 (B)
//        assertThat(parentDto.getChildComments().get(0).getCommentId()).isEqualTo(2L);
//    }
//
//
//    @Test
//    void testUpdateComment() {
//        // given
//        Long commentId = 1L;
//        String newContent = "Updated content";
//        Comment existing = Comment.builder()
//                .commentId(commentId)
//                .content("old content")
//                .build();
//
//        given(commentRepository.findById(commentId)).willReturn(Optional.of(existing));
//
//        // when
//        commentService.updateComment(commentId, newContent);
//
//        // then
//        assertThat(existing.getContent()).isEqualTo(newContent);
//        verify(commentRepository).findById(commentId);
//    }
//
//    @Test
//    void testDeleteComment_noChildren() {
//        // given
//        Long commentId = 1L;
//        Comment existing = Comment.builder()
//                .commentId(commentId)
//                .content("to be deleted")
//                .post(mockPost)
//                .build();
//
//        given(commentRepository.findById(commentId)).willReturn(Optional.of(existing));
//
//        // when
//        commentService.deleteComment(commentId);
//
//        // then
//        assertThat(mockPost.getComment_count()).isEqualTo(0); // 처음 0 - totalDeleted(1) = 0
//        verify(commentRepository).delete(existing);
//    }
//
//    @Test
//    void testDeleteComment_withChildren() {
//        // given
//        Long commentId = 1L;
//        Comment parent = Comment.builder()
//                .commentId(commentId)
//                .content("parent")
//                .post(mockPost)
//                .childComments(new ArrayList<>()) // empty
//                .build();
//
//        Comment child = Comment.builder()
//                .commentId(2L)
//                .content("child comment")
//                .post(mockPost)
//                .build();
//
//        // 계층 구조
//        parent.addChildComment(child);
//
//        // post.comment_count = 0 -> 2개의 댓글 있으므로 나중에 0
//        mockPost.setComment_count(2);
//
//        given(commentRepository.findById(commentId)).willReturn(Optional.of(parent));
//
//        // when
//        commentService.deleteComment(commentId);
//
//        // then
//        verify(commentRepository).delete(parent);
//        // post.comment_count = 2 - 2 = 0
//        assertThat(mockPost.getComment_count()).isEqualTo(0);
//    }
//
//    @Test
//    void testDeleteComment_notFound() {
//        // given
//        Long commentId = 999L;
//        given(commentRepository.findById(commentId)).willReturn(Optional.empty());
//
//        // when & then
//        assertThatThrownBy(() -> commentService.deleteComment(commentId))
//                .isInstanceOf(IllegalArgumentException.class)
//                .hasMessageContaining("Invalid comment ID");
//    }
//
//    @Test
//    void testUpdateComment_notFound() {
//        // given
//        Long commentId = 999L;
//        given(commentRepository.findById(commentId)).willReturn(Optional.empty());
//
//        // when & then
//        assertThatThrownBy(() -> commentService.updateComment(commentId, "newContent"))
//                .isInstanceOf(IllegalArgumentException.class)
//                .hasMessageContaining("해당 commentId의 댓글이 없습니다");
//    }
//
//    @Test
//    void testCreateComment_postNotFound() {
//        // given
//        CommentDTO dto = CommentDTO.builder()
//                .postId(999L)
//                .userId("tester")
//                .content("No post found")
//                .build();
//        given(postRepository.findById(999L)).willReturn(Optional.empty());
//
//        // when & then
//        assertThatThrownBy(() -> commentService.createComment(dto))
//                .isInstanceOf(IllegalArgumentException.class)
//                .hasMessageContaining("해당 postId의 게시글이 없습니다");
//    }
//
//    @Test
//    void testCreateComment_parentNotFound() {
//        // given
//        CommentDTO dto = CommentDTO.builder()
//                .postId(100L)
//                .userId("tester")
//                .content("Trying child comment")
//                .parentCommentId(999L)
//                .build();
//
//        given(postRepository.findById(100L)).willReturn(Optional.of(mockPost));
//        given(commentRepository.findById(999L)).willReturn(Optional.empty());
//
//        // when & then
//        assertThatThrownBy(() -> commentService.createComment(dto))
//                .isInstanceOf(IllegalArgumentException.class)
//                .hasMessageContaining("해당 parentCommentId의 댓글이 없습니다");
//    }
//}
