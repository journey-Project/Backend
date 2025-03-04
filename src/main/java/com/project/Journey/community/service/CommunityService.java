package com.project.Journey.community.service;

import com.project.Journey.community.dto.CommunityDTO;
import com.project.Journey.community.dto.CommunityRequestDTO;
import com.project.Journey.community.dto.CommunityResponseDTO;
import com.project.Journey.community.entity.Community;
import com.project.Journey.community.entity.CommunityImage;
import com.project.Journey.community.repository.CommunityImageRepository;
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

   private final CommunityImageRepository communityImageRepository;

    public CommunityService(CommunityRepository communityRepository, CommunityImageRepository communityImageRepository) {
        this.communityRepository = communityRepository;
        this.communityImageRepository = communityImageRepository;
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


    //게시글 수정
    @Transactional
    public void updateCommunityPostById(Long communityPostId, CommunityResponseDTO communityResponseDTO){
        Community community = communityRepository.findById(communityPostId)
                .orElseThrow(()-> new IllegalArgumentException("해당 CommunityPostId의 게시글이 없습니다"));
        community.updateCountry(communityResponseDTO.getCountry());
        community.updateTitle(communityResponseDTO.getTitle());
        community.updateContent(communityResponseDTO.getContent());
        community.updateUpdatedAt(communityResponseDTO.getUpdated_at());

        // 기존 이미지 리스트 가져오기 (수정 불가능한 리스트 방지)
        List<CommunityImage> existingImages = new ArrayList<>(community.getImages());

        //사용자가 보낸 이미지 리스트
        List<String> updatedImageUrls = communityResponseDTO.getImageUrls();

        // 기존 이미지 중 유지할 이미지 확인
        List<CommunityImage> imagesToKeep = new ArrayList<>();
        for (CommunityImage image : existingImages) {
            if (updatedImageUrls.contains(image.getImageUrl())) {
                imagesToKeep.add(image); // 유지할 이미지 추가
            }
        }

        // 삭제해야 할 이미지 확인
        List<CommunityImage> imagesToRemove = new ArrayList<>();
        for (CommunityImage image : existingImages) {
            if (!updatedImageUrls.contains(image.getImageUrl())) {
                imagesToRemove.add(image); // 삭제할 이미지 추가
            }
        }

        // DB에서 삭제
        communityImageRepository.deleteAll(imagesToRemove);
        community.getImages().removeAll(imagesToRemove);  // 기존 컬렉션에서 제거

        // 새롭게 추가할 이미지 확인
        List<String> existingImageUrls = new ArrayList<>();
        for (CommunityImage image : imagesToKeep) {
            existingImageUrls.add(image.getImageUrl());
        }

        List<CommunityImage> newImages = new ArrayList<>();
        for (String url : updatedImageUrls) {
            if (!existingImageUrls.contains(url)) { // 기존에 없는 경우만 추가
                CommunityImage newImage = new CommunityImage(null, url, community);
                newImages.add(newImage);
            }
        }

        // 기존 리스트에 유지할 이미지와 새 이미지를 직접 추가
        community.getImages().addAll(newImages);

        //  변경 사항 저장
        communityRepository.save(community);



    }
    //게시글 삭제
    @Transactional
    public void deleteCommunityPost(Long communityPostId){
        Community community = communityRepository.findById(communityPostId)
                .orElseThrow(()-> new EntityNotFoundException("커뮤니티 게시글을 찾을 수 없습니다."));
        communityRepository.delete(community);
    }

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
