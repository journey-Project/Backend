package com.project.Journey.community.service;

import com.project.Journey.community.dto.CommunityDTO;
import com.project.Journey.community.dto.CommunityRequestDTO;
import com.project.Journey.community.dto.CommunityResponseDTO;
import com.project.Journey.community.entity.Community;
import com.project.Journey.community.entity.CommunityImage;
import com.project.Journey.community.repository.CommunityRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
    public Long saveCommunityPost(CommunityRequestDTO communityRequestDTO){
        Community community = Community.builder()
                .user_id(communityRequestDTO.getUser_id())
                .country(communityRequestDTO.getCountry())
                .title(communityRequestDTO.getTitle())
                .content(communityRequestDTO.getContent())
                .view_count(0)
                .comment_count(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                //.images(communityRequestDTO.getImageUrls())
                .build();

        List<CommunityImage> images = new ArrayList<>();
        for(String url : communityRequestDTO.getImageUrls()){
            CommunityImage image = new CommunityImage(null, url, community);
            images.add(image);
        }
        community.setImages(images);
        Community savedCommunity = communityRepository.save(community);
        return savedCommunity.getCommunityPostId();
    }


    /*
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
*/

    //특정 게시글 조회
    public CommunityResponseDTO getPostByCommunityPostId(Long communitypostid){
        Community community = communityRepository.findById(communitypostid)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));
        community.setView_count(community.getView_count()+1);
        communityRepository.save(community);

        List<CommunityImage> communityImages = community.getImages();
        List<String> images = new ArrayList<>();
        for(CommunityImage communityImage : communityImages){
            images.add(communityImage.getImageUrl());
        }

        CommunityResponseDTO communityResponseDTO
                = new CommunityResponseDTO(
                community.getCommunityPostId(),
                community.getUser_id(),
                community.getCountry(),
                community.getTitle(),
                community.getContent(),
                community.getView_count(),
                community.getComment_count(),
                community.getCreatedAt(),
                community.getUpdatedAt(),
                community.getProfileImageUrl(),
                images

        );
        return communityResponseDTO;
    }


/*

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
*/

    /*
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
*/

    //게시글 수정

    //게시글 삭제

    // 국가별 특정 기간의 게시글 페이징 조회
    public Page<CommunityResponseDTO> getPostsByDateRange(String startDate, String endDate, String country, Pageable pageable){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime startDateTime = LocalDate.parse(startDate, formatter).atStartOfDay();
        LocalDateTime endDateTime = LocalDate.parse(endDate, formatter).atTime(23, 59, 59);

        // 특정 기간과 국가를 기준으로 게시글 조회 (페이징 포함)
        Page<Community> communityPage =communityRepository.findByCreatedAtBetweenAndCountry(startDateTime, endDateTime, country, pageable);


        // Community 엔티티 -> CommunityResponseDto로 변환
        List<CommunityResponseDTO> communityResponseDTOS = new ArrayList<>();
        for(Community community : communityPage.getContent()){
            CommunityResponseDTO responseDTO = new CommunityResponseDTO();
            responseDTO.setCommunityPostId(community.getCommunityPostId());
            responseDTO.setUser_id(community.getUser_id());
            responseDTO.setCountry(community.getCountry());
            responseDTO.setTitle(community.getTitle());
            responseDTO.setContent(community.getContent());
            responseDTO.setView_count(community.getView_count());
            responseDTO.setComment_count(community.getComment_count());
            responseDTO.setCreated_at(community.getCreatedAt());
            responseDTO.setUpdated_at(community.getUpdatedAt());
            responseDTO.setProfileImageUrl(community.getProfileImageUrl());

            List<String> imageUrls = new ArrayList<>();
            for(CommunityImage image : community.getImages()){
                imageUrls.add(image.getImageUrl());
            }
            responseDTO.setImageUrls(imageUrls);
            communityResponseDTOS.add(responseDTO);
        }
        // 변환된 데이터를 Page 객체로 감싸서 반환
        return new PageImpl<>(communityResponseDTOS, pageable, communityPage.getTotalElements());
    }


}
