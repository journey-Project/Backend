package com.project.Journey.story.entity;

import com.project.Journey.login.member.domain.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Story {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imageUrl;

    private LocalDateTime createdAt;

    private LocalDateTime expireAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private Member author;

    public Story(Member author, String imageUrl, LocalDateTime expireAt){
        this.author=author;
        this.imageUrl=imageUrl;
        this.expireAt=expireAt;
        this.createdAt=LocalDateTime.now();
    }


}
