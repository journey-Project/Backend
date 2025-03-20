package com.project.Journey.community.controller;



import com.project.Journey.board.dto.PostDTO;
import com.project.Journey.board.exception.PostException;
import com.project.Journey.board.service.PostService;
import com.project.Journey.community.dto.CommunityDTO;
import com.project.Journey.community.dto.CommunityRequestDTO;
import com.project.Journey.community.dto.CommunityResponseDTO;
import com.project.Journey.community.service.CommunityService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "커뮤니티 게시글 관리", description = "커뮤니티 관련 API")
public class CommunityController {
    private final CommunityService communityService;


    @PostMapping(value = "/api/community/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Long> createPost(
            @RequestPart("data") @Parameter(description = "게시글 정보", required = true) CommunityRequestDTO communityRequestDTO,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        if (communityRequestDTO == null) {
            throw new IllegalArgumentException("요청 데이터가 올바르지 않습니다.");
        }
        if (images== null) {
            throw new IllegalArgumentException("이미지 리스트가 null입니다");
        }

        try{

            log.info("Request DTO: {}", communityRequestDTO);
            log.info("Received {} images", (images != null ? images.size() : 0));
            Long savedPostId = communityService.saveCommunityPost(communityRequestDTO, images);

            return ResponseEntity.ok(savedPostId);
        } catch (Exception e){
            throw new PostException("게시글 저장 중 오류가 발생했습니다.", HttpStatus.BAD_REQUEST);
        }

    }


    //페이지 네이션 적용된
    //@GetMapping("/api/community/getPostsByPage")
    //public Page<CommunityDTO> getCommunityPosts(@RequestParam String country, Pageable pageable) {
   //     return communityService.getPostsByCountry(country, pageable);
   // }

    // 특정 게시글 조회 (조회수 증가 반영)
    @GetMapping("/api/community/getPostByPostId/{communityPostId}")
    public CommunityResponseDTO getCommunityPost(@PathVariable Long communityPostId) {
        return communityService.getPostByCommunityPostId(communityPostId);
    }

    //국가별 커뮤니티 특정 기간의 게시글 페이징 조회
    @GetMapping("/api/community/searchPosts")
    public Page<CommunityResponseDTO> getPostsByDateRangeAndCountry(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam String country,
            Pageable pageable
    ){

        return communityService.getPostsByDateRange(startDate, endDate,country, pageable);
    };

    //게시글 수정
    @PutMapping("/api/community/update/{CommunityPostId}")
    public ResponseEntity<String> updateCommunityPost(@PathVariable Long CommunityPostId,
                                                      @RequestBody CommunityResponseDTO communityResponseDTO){
        communityService.updateCommunityPostById(CommunityPostId, communityResponseDTO);
        return ResponseEntity.ok("커뮤니티 게시글이 성공적으로 수정되었습니다");
    }

    //게시글 삭제
    @DeleteMapping("/api/community/DeletePosts/{CommunityPostId}")
    public ResponseEntity<Void> deleteCommunityPost(@PathVariable Long CommunityPostId){
        communityService.deleteCommunityPost(CommunityPostId);
        return ResponseEntity.noContent().build();
    }
}

