package com.project.Journey.companion.comment.entity;

import com.project.Journey.companion.entity.Post;
import com.project.Journey.login.member.domain.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member writer;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    private Comment parentComment;

    @Column(nullable = false)
    @Builder.Default
    private boolean isActive = true;      // true = 노출, false = 삭제

    @Builder.Default
    @Column(nullable = false)
    private int replyCount = 0;             // 자식 대댓글 수

    public void setContent(String content) {
        this.content = content;
    }
    public void deactivate() {
        this.isActive = false;
    }
    public void incrementReplyCount() {
        this.replyCount++;
    }
    public void decrementReplyCount() {
        if (this.replyCount > 0) this.replyCount--;
    }
    public boolean isActive() {
        return isActive;
    }

    @PrePersist
    public void onPrePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onPreUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
