package com.project.Journey.community.repository;

import com.project.Journey.community.entity.Community;
import com.project.Journey.community.entity.CommunityImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommunityImageRepository extends JpaRepository<CommunityImage, Long> {
    void deleteByCommunity(Community community);
}