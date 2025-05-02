package com.project.Journey.community.service;

import com.project.Journey.awss3.S3Service;
import com.project.Journey.community.dto.*;
import com.project.Journey.community.entity.Community;
import com.project.Journey.community.entity.CommunityImage;
import com.project.Journey.community.paging.CommunitySpecification;
import com.project.Journey.community.paging.Pagination;
import com.project.Journey.community.repository.CommunityImageRepository;
import com.project.Journey.community.repository.CommunityRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.internal.util.collections.ReadOnlyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CommunityService {

   @Autowired
    private final CommunityRepository communityRepository;

   private final CommunityImageRepository communityImageRepository;

    private final S3Service s3Service;

    public CommunityService(CommunityRepository communityRepository, CommunityImageRepository communityImageRepository, S3Service s3Service) {
        this.communityRepository = communityRepository;
        this.communityImageRepository = communityImageRepository;
        this.s3Service = s3Service;
    }


    //게시글 저장
    @Transactional
    public Long saveCommunityPost(CommunityRequestDTO communityRequestDTO, List<MultipartFile> images) throws IOException {
        Community community = Community.builder()
                .user_id(communityRequestDTO.getUser_id())
                .country(communityRequestDTO.getCountry())
                .title(communityRequestDTO.getTitle())
                .content(communityRequestDTO.getContent())
                .viewCount(0)
                .comment_count(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        //1. 게시글을 먼저 저장
        Community savedCommunity = communityRepository.save(community); // DB에 먼저 저장

        //2. 이미지 저장을 위한 리스트 생성
        List<CommunityImage> imageEntities = new ArrayList<>();

        //3. 이미지 업로드 및 CommunityImage 엔티티 생성
        if(images != null && !images.isEmpty()){
            for (MultipartFile image : images) {
                // S3에 업로드 후 URL 반환
                String imageUrl = s3Service.uploadApplicationImage(image);
                CommunityImage communityImage = new CommunityImage(null, imageUrl, savedCommunity);
                imageEntities.add(communityImage);
            }

        }

       // community.setImages(imageEntities);
        //communityRepository.save(savedCommunity);
        communityImageRepository.saveAll(imageEntities);
        return savedCommunity.getCommunityPostId();
    }

    //특정 게시글 조회
    public CommunityResponseDTO getPostByCommunityPostId(Long communitypostid){
        Community community = communityRepository.findById(communitypostid)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));
        community.setViewCount(community.getViewCount()+1);
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
                community.getViewCount(),
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
    public void updateCommunityPostById(Long communityPostId,
                                        CommunityResponseDTO communityResponseDTO,
                                        List<MultipartFile> newImages) {

        Community community = communityRepository.findById(communityPostId)
                .orElseThrow(() -> new IllegalArgumentException("해당 CommunityPostId의 게시글이 없습니다"));

        // 게시글 기본 정보 수정
        community.updateCountry(communityResponseDTO.getCountry());
        community.updateTitle(communityResponseDTO.getTitle());
        community.updateContent(communityResponseDTO.getContent());
        community.updateUpdatedAt(LocalDateTime.now());

        // 기존 이미지 가져오기
        List<CommunityImage> existingImages = new ArrayList<>(community.getImages());
        List<String> updatedImageUrls = communityResponseDTO.getImageUrls();

        // 유지할 이미지
        List<CommunityImage> imagesToKeep = existingImages.stream()
                .filter(img -> updatedImageUrls.contains(img.getImageUrl()))
                .collect(Collectors.toList());

        // 삭제할 이미지
        List<CommunityImage> imagesToRemove = existingImages.stream()
                .filter(img -> !updatedImageUrls.contains(img.getImageUrl()))
                .collect(Collectors.toList());

        // 삭제 수행
        communityImageRepository.deleteAll(imagesToRemove);
        community.getImages().removeAll(imagesToRemove);

        // 기존 유지 이미지 URL 목록
        List<String> existingImageUrls = imagesToKeep.stream()
                .map(CommunityImage::getImageUrl)
                .collect(Collectors.toList());

        // 새 이미지 업로드 및 추가
        if (newImages != null && !newImages.isEmpty()) {
            for (MultipartFile file : newImages) {
                if (file != null && !file.isEmpty()) {
                    String uploadedUrl = s3Service.uploadApplicationImage(file); // S3 또는 저장소에 업로드
                    if (!existingImageUrls.contains(uploadedUrl)) {
                        CommunityImage newImage = new CommunityImage(null, uploadedUrl, community);
                        community.getImages().add(newImage);
                    }
                }
            }
        }

        // 변경 내용 저장
        communityRepository.save(community);
    }

    //게시글 삭제
    @Transactional
    public void deleteCommunityPost(Long communityPostId){
        Community community = communityRepository.findById(communityPostId)
                .orElseThrow(()-> new EntityNotFoundException("커뮤니티 게시글을 찾을 수 없습니다."));

        //이미지 url들을 S3에서 삭제
        if(community.getImages() != null){
            for(CommunityImage communityImage : community.getImages()){
                s3Service.deleteS3Image(communityImage.getImageUrl());
            }
        }

        //DB에서 게시글 삭제
        communityRepository.delete(community);
    }






    @Transactional
    public Map<String, Object> getHotPosts(int page, int size) {
        int pageIndex = page - 1; // 1부터 시작하도록 조정
        if (pageIndex < 0) {
            pageIndex = 0; // 잘못된 요청 방지
        }

        Pageable pageable = PageRequest.of(pageIndex, size, Sort.by(Sort.Direction.DESC, "viewCount"));
        Page<Community> communityPage = communityRepository.findAll(pageable);

        List<HotPostResponseDTO> posts = communityPage.getContent().stream()
                .map(community -> new HotPostResponseDTO(
                        community.getCommunityPostId(),
                        community.getCountry(),
                        community.getTitle(),
                        community.getCreatedAt()
                ))
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("totalCount", communityPage.getTotalElements()); // 전체 게시글 수
        result.put("posts", posts); // 게시글 리스트
        result.put("currentPage", page); // 요청한 페이지 번호

        return result;
    }


    //오늘은 어떤 이야기를 나누었을까요? -> 커뮤니티 게시글 중 조회수가 가장 높은 게시글 지정된 개수만큼 가져오기
    public List<CommunityMainHotPostDTO> getHotPosts(int count) {
        List<Community> hotPosts = communityRepository.findTopCommunityViewCount(PageRequest.of(0, count));

        return hotPosts.stream().map(post -> CommunityMainHotPostDTO.builder()
                .postId(post.getCommunityPostId())
                .user_id(post.getUser_id())
                .profileImageUrl(post.getProfileImageUrl())
                .imageUrls(post.getImages().stream()
                        .map(image -> image.getImageUrl())
                        .collect(Collectors.toList()))
                .content(post.getContent())
                .country(post.getCountry())
                .build()
        ).collect(Collectors.toList());
    }




    public CommunitySearchResponseDTO searchCommunityPosts(SearchDTO searchDTO) {
        Pageable pageable = PageRequest.of(searchDTO.getPage() - 1, searchDTO.getRecordSize());
        Specification<Community> spec = CommunitySpecification.searchWithFilters(searchDTO);

        Page<Community> resultPage = communityRepository.findAll(spec, pageable);
        List<CommunityPageResponseDTO> communityList = resultPage.getContent().stream()
                .map(CommunityPageResponseDTO::fromEntity)
                .collect(Collectors.toList());

        Pagination pagination = new Pagination((int) resultPage.getTotalElements(), searchDTO);

        return new CommunitySearchResponseDTO(communityList, pagination);
    }
}