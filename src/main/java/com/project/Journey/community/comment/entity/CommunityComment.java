package com.project.Journey.community.comment.entity;

import com.project.Journey.community.entity.Community;
import com.project.Journey.login.member.domain.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "community_comment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CommunityComment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @ManyToOne(fetch = FetchType.LAZY)                       // ★ 작성자
    @JoinColumn(name = "member_id", nullable = false)
    private Member writer;

    @ManyToOne(fetch = FetchType.LAZY)                       // ★ 게시글
    @JoinColumn(name = "community_post_id", nullable = false)
    private Community community;

    @ManyToOne(fetch = FetchType.LAZY)                       // ★ 대댓글(1단)
    @JoinColumn(name = "parent_comment_id")
    private CommunityComment parentComment;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private boolean isActive = true;

    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL)
    private List<CommunityComment> replies = new ArrayList<>();


    @Column(nullable = false)
    private int replyCount = 0;

    public void updateContent(String content)      { this.content = content; }
    public void incrementReplyCount()              { this.replyCount++; }
    public void decrementReplyCount()              { if (this.replyCount > 0) this.replyCount--; }
    public void deactivate()                       { this.isActive = false; }

    @PrePersist
    public void onCreate() {
        this.createdAt  = LocalDateTime.now();
        this.updatedAt  = this.createdAt;
    }
    @PreUpdate
    public void onUpdate()  { this.updatedAt = LocalDateTime.now(); }

    @Transient
    public Long getMemberId() {                       // 필요하면 DTO 변환 시 사용
        return writer != null ? writer  .getId() : null;
    }

    @Builder
    public CommunityComment(Member writer, Community community, CommunityComment parentComment, String content) {
        this.writer = writer;
        this.community = community;
        this.parentComment = parentComment;
        this.content = content;
        this.isActive = true;
        this.replyCount = 0;
    }
}
