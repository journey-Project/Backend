/*
package com.project.Journey.Community;

import com.project.Journey.community.entity.Community;
import com.project.Journey.community.entity.CommunityImage;
import com.project.Journey.community.repository.CommunityImageRepository;
import com.project.Journey.community.repository.CommunityRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@SpringBootTest
@Transactional // 테스트 후 롤백을 원하지 않으면 제거
public class CommunityDummyDataTest {

    @Autowired
    private CommunityRepository communityRepository;

    @Autowired
    private CommunityImageRepository imageRepository;

    @Test
    public void insertDummyData() {
        for (int i = 1; i <= 168; i++) {
            Community community = Community.builder()
                    .user_id("user" + (i % 20)) // 20명 유저 반복
                    .country(i % 2 == 0 ? "국내" : "해외")
                    .title("더미 제목 " + i)
                    .content("이것은 더미 내용입니다. 더미 번호: " + i)
                    .viewCount(0)
                    .comment_count(0)
                    .createdAt(LocalDateTime.now().minusDays(i))
                    .updatedAt(LocalDateTime.now().minusDays(i))
                    .build();

            Community saved = communityRepository.save(community);

            // 이미지 포함: 1~7번만
            if (i <= 7) {
                CommunityImage image = CommunityImage.builder()
                        .imageUrl("https://via.placeholder.com/600x400.png?text=Dummy+" + i)
                        .community(saved)
                        .build();
                imageRepository.save(image);
            }
        }
    }
}
*/