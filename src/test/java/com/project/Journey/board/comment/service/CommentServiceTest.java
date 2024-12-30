package com.project.Journey.board.comment.service;

import com.project.Journey.board.comment.domain.Comment;
import com.project.Journey.board.comment.domain.CommentDTO;
import com.project.Journey.board.comment.repository.CommentRepository;
import com.project.Journey.board.entity.Post;
import com.project.Journey.board.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private CommentService commentService;

    private Post post;
    private Comment comment;

    @BeforeEach
    void setup() {
        // 샘플 Post
        post = Post.builder()
                .post_id(1L)
                .user_id("testUser")
                .title("Test Title")
                .content("Test Content")
                .destination("Test Destination")
                .view_count(0)
                .comment_count(0)
                .created_at(LocalDateTime.now())
                .updated_at(LocalDateTime.now())
                .build();

        // 샘플 Comment
        comment = Comment.builder()
                .comment_id(100L)
                .user_id("commentUser")
                .content("This is a comment")
                .created_at(LocalDateTime.now())
                .updated_at(LocalDateTime.now())
                .post(post)
                .build();
    }

    @Test
    @DisplayName("createComment_Success - 댓글 생성 테스트")
    void createComment_Success() {
        // given
        CommentDTO commentDTO = CommentDTO.builder()
                .post_id(post.getPost_id())
                .user_id("commentUser")
                .content("New Comment")
                .build();

        when(postRepository.findById(post.getPost_id())).thenReturn(Optional.of(post));
        // commentRepository.save(...)가 호출되면 comment 엔티티를 리턴하자(실제로는 ID가 부여된 엔티티)
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> {
            Comment saved = invocation.getArgument(0);
            saved.setComment_id(999L); // DB에서 생성된 PK라고 가정
            return saved;
        });

        // when
        Long resultId = commentService.createComment(commentDTO);

        // then
        assertThat(resultId).isEqualTo(999L);
        assertThat(post.getComment_count()).isEqualTo(1);  // 댓글 1개 증가
        verify(commentRepository, times(1)).save(any(Comment.class));
        verify(postRepository, never()).save(any(Post.class));
        // @Transactional로 post 변경사항 flush 시 반영
    }

    @Test
    @DisplayName("createComment_PostNotFound_ShouldThrowException - 게시글 없는 경우 예외")
    void createComment_PostNotFound_ShouldThrowException() {
        // given
        CommentDTO commentDTO = CommentDTO.builder()
                .post_id(9999L)
                .user_id("commentUser")
                .content("No post found")
                .build();

        when(postRepository.findById(9999L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class, () -> commentService.createComment(commentDTO));
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    @DisplayName("getCommentsByPostId_ReturnsList - 게시글 댓글 조회")
    void getCommentsByPostId_ReturnsList() {
        // given
        Long postId = post.getPost_id();
        when(commentRepository.findByPost_Post_id(postId)).thenReturn(List.of(comment));

        // when
        List<CommentDTO> result = commentService.getCommentsByPostId(postId);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getComment_id()).isEqualTo(comment.getComment_id());
        assertThat(result.get(0).getContent()).isEqualTo(comment.getContent());
        verify(commentRepository, times(1)).findByPost_Post_id(postId);
    }

    @Test
    @DisplayName("updateComment_Success - 댓글 수정 테스트")
    void updateComment_Success() {
        // given
        Long commentId = comment.getComment_id();
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        String newContent = "Updated Content";

        // when
        commentService.updateComment(commentId, newContent);

        // then
        assertThat(comment.getContent()).isEqualTo(newContent);
        assertThat(comment.getUpdated_at()).isAfter(comment.getCreated_at());
        verify(commentRepository, times(1)).findById(commentId);
    }

    @Test
    @DisplayName("updateComment_NotFound_ShouldThrowException - 없는 댓글 수정 예외")
    void updateComment_NotFound_ShouldThrowException() {
        // given
        Long nonExistingId = 9999L;
        when(commentRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class, () ->
                commentService.updateComment(nonExistingId, "irrelevant"));
        verify(commentRepository, times(1)).findById(nonExistingId);
    }

    @Test
    @DisplayName("deleteComment_Success - 댓글 삭제 테스트")
    void deleteComment_Success() {
        // given
        Long commentId = comment.getComment_id();
        // comment 연결된 post가 존재
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        // when
        commentService.deleteComment(commentId);

        // then
        verify(commentRepository, times(1)).delete(comment);
        // post.comment_count 감소 (기본 0이면 감소 안 할 수도)
        // 여기서 comment_count=0 -> 삭제하면 -1 안 되도록 if문에서 막음
        assertThat(post.getComment_count()).isEqualTo(0);
    }

    @Test
    @DisplayName("deleteComment_NotFound_ShouldThrowException - 없는 댓글 삭제 예외")
    void deleteComment_NotFound_ShouldThrowException() {
        // given
        Long nonExistingId = 9999L;
        when(commentRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class, () ->
                commentService.deleteComment(nonExistingId));
        verify(commentRepository, never()).delete(any(Comment.class));
    }
}