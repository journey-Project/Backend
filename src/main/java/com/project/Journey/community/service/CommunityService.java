package com.project.Journey.community.service;

import com.project.Journey.community.dto.CommunityDTO;
import com.project.Journey.community.entity.Community;
import com.project.Journey.community.repository.CommunityRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommunityService {

   @Autowired
    private final CommunityRepository communityRepository;

    public CommunityService(CommunityRepository communityRepository) {
        this.communityRepository = communityRepository;
    }


    //게시글 저장
    @Transactional
    public Long saveCommunityPost(CommunityDTO communityDTO){
        Community community = Community.builder()
                .title(communityDTO.getTitle())
                .content(communityDTO.getContent())
                .country(communityDTO.getCountry())
                .user_id(communityDTO.getUser_id())
                .view_count(0)
                .comment_count(0)
                .created_at(LocalDateTime.now())
                .updated_at(LocalDateTime.now())
                .profileImageUrl(communityDTO.getProfileImageUrl())
                .ImageUrl(communityDTO.getImageUrl())
                .build();
        return communityRepository.save(community).getCommunityPostId();
    }

    //페이지 네이션 적용 - community post 가져오기
    public Page<CommunityDTO> getPostsByCountry(String country, Pageable pageable) {
        return communityRepository.findByCountry(country, pageable)
                .map(community -> new CommunityDTO(
                        community.getUser_id(),
                        community.getCountry(),
                        community.getTitle(),
                        community.getContent(),
                        community.getView_count(),
                        community.getComment_count(),
                        community.getCreated_at(),
                        community.getUpdated_at(),
                        community.getProfileImageUrl(),
                        community.getImageUrl()
                ));
    }


    public CommunityDTO getPostByCommunityPostId(Long communitypostid) {
        return communityRepository.findById(communitypostid)
                .map(community -> {
                    community.setView_count(community.getView_count()+1);
                    communityRepository.save(community);
                    return new CommunityDTO(
                            community.getUser_id(),
                            community.getCountry(),
                            community.getTitle(),
                            community.getContent(),
                            community.getView_count(),
                            community.getComment_count(),
                            community.getCreated_at(),
                            community.getUpdated_at(),
                            community.getProfileImageUrl(),
                            community.getImageUrl()
                    );
                }).orElseThrow(() -> new RuntimeException("Post not found"));
    }


}
