package com.project.Journey.board.comment.domain;

import com.project.Journey.board.entity.Post;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @Column(name = "user_id", nullable = false) // 데이터베이스 컬럼명 명시
    private String userId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // (1) 게시글(부모)이 되는 Post
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    // (2) 부모 댓글
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    private Comment parentComment;

    // (3) 자식 댓글 목록 (대댓글들)
    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Comment> childComments = new ArrayList<>();

    // (4) (선택) 깊이(계층) 표시
    private int depth;  // 0 = 최상위, 1 = 대댓글, 2 = 대대댓글...

    public void updateComment(String newContent) {
        this.content = newContent;
        this.updatedAt = LocalDateTime.now();
    }

    // 대댓글 추가 편의 메서드 (양방향 연관관계 처리)
    public void addChildComment(Comment child) {
        this.childComments.add(child);
        child.setParentComment(this);
    }
}
