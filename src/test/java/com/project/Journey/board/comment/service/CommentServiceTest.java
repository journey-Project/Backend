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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 단위 테스트:
 *  - createComment(): 부모댓글/대댓글 시나리오
 *  - getAllCommentsByPostId(): 재귀 구조로 childComments 변환
 *  - updateComment(): 정상 수정 / 예외
 *  - deleteComment(): 댓글 삭제 시, commentCount 감소
 */
@ExtendWith(SpringExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private CommentService commentService;

    private Post post;
    private Comment parentComment;
    private Comment childComment;

    @BeforeEach
    void setup() {
        // 샘플 게시글
        post = Post.builder()
                .postId(100L)
                .user_id("user123")
                .title("Post Title")
                .content("Post Content")
                .view_count(0)
                .comment_count(0)
                .created_at(LocalDateTime.now())
                .updated_at(LocalDateTime.now())
                .build();

        // 최상위(부모) 댓글
        parentComment = Comment.builder()
                .commentId(1L)
                .userId("Ashton")
                .content("parent Comment")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .post(post)
                .depth(0)
                .childComments(new ArrayList<>())
                .build();

        // 자식(대댓글)
        childComment = Comment.builder()
                .commentId(2L)
                .userId("Kevin")
                .content("Child Comment")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .post(post)
                .parentComment(parentComment)
                .depth(1)
                .build();
    }

    @Test
    @DisplayName("createComment - 최상위 댓글 생성 시 post.comment_count 증가, depth=0")
    void createComment_Success() {
        // given
        CommentDTO commentDTO = CommentDTO.builder()
                .postId(post.getPostId())
                .userId("Ashton")
                .content("Parent Comment")
                .parentCommentId(null) // 최상위
                .build();

        when(postRepository.findById(post.getPostId())).thenReturn(Optional.of(post));
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> {
            Comment saved = invocation.getArgument(0);
            saved.setCommentId(999L); // DB에서 생성되는 PK라고 가정
            return saved;
        });

        // when
        Long newCommentId = commentService.createComment(commentDTO);

        // then
        assertThat(newCommentId).isEqualTo(999L);
        assertThat(post.getComment_count()).isEqualTo(1);  // comment_count +1
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    @DisplayName("createComment - 대댓글 생성 시 depth=parent.depth+1")
    void createComment_ChildComment() {
        // given
        CommentDTO dto = CommentDTO.builder()
                .postId(post.getPostId())
                .userId("Kevin")
                .content("Child Comment")
                .parentCommentId(parentComment.getCommentId()) // 대댓글
                .build();

        when(postRepository.findById(post.getPostId())).thenReturn(Optional.of(post));
        when(commentRepository.findById(parentComment.getCommentId())).thenReturn(Optional.of(parentComment));
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> {
            Comment saved = invocation.getArgument(0);
            saved.setCommentId(888L);
            return saved;
        });

        // when
        Long childId = commentService.createComment(dto);

        // then
        assertThat(childId).isEqualTo(888L);
        assertThat(post.getComment_count()).isEqualTo(1);
        // parentComment.depth=0 이므로 child.depth=1 이어야 함
        verify(commentRepository, times(1)).save(argThat(c -> c.getDepth() == 1));
    }

    @Test
    @DisplayName("getAllCommentsByPostId - 최상위 댓글만 조회 후 childComments까지 재귀 변환")
    void getAllCommentsByPostId() {
        // given
        // parentComment의 자식목록에 childComment 추가
        parentComment.getChildComments().add(childComment);

        // Mock: 최상위 댓글만 리턴
        when(commentRepository.findByPost_PostIdAndParentCommentIsNull(post.getPostId()))
                .thenReturn(List.of(parentComment));

        // when
        List<CommentDTO> result = commentService.getAllCommentsByPostId(post.getPostId());

        // then
        // result에는 최상위 댓글만
        assertThat(result).hasSize(1);

        CommentDTO parentDTO = result.get(0);
        assertThat(parentDTO.getCommentId()).isEqualTo(parentComment.getCommentId());
        // 자식 목록이 1개인지 확인
        assertThat(parentDTO.getChildComments()).hasSize(1);

        CommentDTO childDTO = parentDTO.getChildComments().get(0);
        assertThat(childDTO.getCommentId()).isEqualTo(childComment.getCommentId());
        assertThat(childDTO.getDepth()).isEqualTo(childComment.getDepth());

        verify(commentRepository, times(1))
                .findByPost_PostIdAndParentCommentIsNull(post.getPostId());
    }

    @Test
    @DisplayName("updateComment - 정상 수정")
    void updateComment_Success() {
        // given
        Long commentId = parentComment.getCommentId();
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(parentComment));

        String newContent = "Updated Parent Content";

        // when
        commentService.updateComment(commentId, newContent);

        // then
        assertThat(parentComment.getContent()).isEqualTo(newContent);
        assertThat(parentComment.getUpdatedAt()).isAfter(parentComment.getCreatedAt());
        verify(commentRepository, times(1)).findById(commentId);
        verify(commentRepository, never()).save(any());
        // @Transactional로 인해 flush 시점에 반영
    }

    @Test
    @DisplayName("updateComment - 없는 댓글이면 예외")
    void updateComment_NotFound() {
        // given
        when(commentRepository.findById(9999L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> commentService.updateComment(9999L, "irrelevant"));
    }

    @Test
    @DisplayName("deleteComment - 댓글 삭제 시 post.comment_count 감소")
    void deleteComment_Success() {
        // given
        post.setComment_count(2);
        Long commentId = parentComment.getCommentId();
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(parentComment));

        // when
        commentService.deleteComment(commentId);

        // then
        verify(commentRepository, times(1)).delete(parentComment);
        assertThat(post.getComment_count()).isEqualTo(1);
    }

    @Test
    @DisplayName("deleteComment - 없는 댓글이면 예외")
    void deleteComment_NotFound() {
        // given
        when(commentRepository.findById(9999L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> commentService.deleteComment(9999L));
    }
}