package com.project.Journey.community.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "community_images")
public class CommunityImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "imageId")
    private Long imageId;

    //이미지 url
    @Column(nullable = false)
    private String imageUrl;

    //community와 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "communityPostId", nullable = false)
    private Community community;
}
