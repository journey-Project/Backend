package com.project.Journey.board.comment.service;


import com.project.Journey.board.comment.domain.Comment;
import com.project.Journey.board.comment.domain.CommentDTO;
import com.project.Journey.board.comment.repository.CommentRepository;
import com.project.Journey.board.entity.Post;
import com.project.Journey.board.repository.PostRepository;
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

    // 댓글 생성
    @Transactional
    public Long createComment(CommentDTO commentDTO) {
        // 댓글 달릴 대상 게시글(post) 찾기
        Post post = postRepository.findById(commentDTO.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("해당 post_id의 게시글이 없습니다"));

        // Comment Entity 생성
        Comment comment = Comment.builder()
                .userId(commentDTO.getUserId())
                .content(commentDTO.getContent())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .post(post)
                .build();

        // 댓글 저장
        Comment savedComment = commentRepository.save(comment);

        // 게시글의 comment_count wmdrk
        post.setComment_count(post.getComment_count() + 1);
        // postRepository.save(post); // 영속성 컨텍스트 내에서 post가 변경되면 @Transactional 커밋 시점에 반영됨

        return savedComment.getCommentId();
    }

    // 게시글별 모든 댯굴 조회
    public List<CommentDTO> getCommentsByPostId(Long postId) {
        List<Comment> comments = commentRepository.findByPost_PostId(postId);
        List<CommentDTO> result = new ArrayList<>();

        for (Comment comment : comments) {
            CommentDTO dto = CommentDTO.builder()
                    .commentId(comment.getCommentId())
                    .userId(comment.getUserId())
                    .content(comment.getContent())
                    .createdAt(comment.getCreatedAt())
                    .updatedAt(comment.getUpdatedAt())
                    .postId(comment.getPost().getPostId())
                    .build();
            result.add(dto);
        }
        return result;
    }

    // 댓글 수정
    @Transactional
    public void updateComment(Long commentId, String newContent) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 comment_id의 댓글이 없습니다"));

        comment.updateComment(newContent);
        // commentRepository.save(comment); // @Transactional로 인해 변경사항 자동 반영
    }

    // 댓글 삭제
    @Transactional
    public void deleteComment(Long commentId) {
        // 삭제 대상 댓글 찾기
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 comment_id의 댓글이 없습니다."));

        // 연관된 Post 가져오기 (댓글 수 감소를 위해)
        Post post = comment.getPost();

        // 댓글 삭제
        commentRepository.delete(comment);

        // 게시글의 comment_count 감소
        if (post.getComment_count() > 0) {
            post.setComment_count(post.getComment_count() - 1);
        }
        // postRepository.save(post);
    }
}
