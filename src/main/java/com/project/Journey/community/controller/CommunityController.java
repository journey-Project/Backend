package com.project.Journey.community.controller;



import com.project.Journey.board.dto.PostDTO;
import com.project.Journey.board.exception.PostException;
import com.project.Journey.board.service.PostService;
import com.project.Journey.community.dto.*;
import com.project.Journey.community.entity.Community;
import com.project.Journey.community.service.CommunityService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    // 특정 게시글 조회 (조회수 증가 반영)
    @GetMapping("/api/community/getPostByPostId/{communityPostId}")
    public CommunityResponseDTO getCommunityPost(@PathVariable Long communityPostId) {
        return communityService.getPostByCommunityPostId(communityPostId);
    }

    //국가별 커뮤니티 특정 기간의 게시글 페이징 조회
    /*
    @GetMapping("/api/community/posts")
    public ResponseEntity<Map<String, Object>> getPostsByDateRange(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(value = "page", defaultValue = "1") int page,  // 기본값 1
            @RequestParam(value = "size", defaultValue = "10") int size) {

        Map<String, Object> response = communityService.getPostsByDateRange(startDate, endDate, page, size);
        return ResponseEntity.ok(response);
    }
*/

    //오늘의 핫 게시물 기능 (페이지네이션 추가)
    @GetMapping("/api/community/hot-posts")
    public ResponseEntity<Map<String, Object>> getHotPosts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "3") int size) {

        Map<String, Object> response = communityService.getHotPosts(page, size);
        return ResponseEntity.ok(response);
    }




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

    //메인 페이지 - 오늘은 어떤 이야기를 나누었을까요? (조회수가 가장 높은 게시글 count 수만큼 반환)
    @GetMapping("/api/community/main-hot-posts")
    public ResponseEntity<List<CommunityMainHotPostDTO>> getHotPosts(@RequestParam(defaultValue = "3") int count) {
        List<CommunityMainHotPostDTO> hotPosts = communityService.getHotPosts(count);
        return ResponseEntity.ok(hotPosts);
    }

    //검색 API 개발

    @GetMapping("/api/community/search")
    public CommunitySearchResponseDTO searchPosts(
            @RequestParam String country,
            @RequestParam(required = false) Long number,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String writer,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int recordSize
    ) {
        SearchDTO searchDTO = new SearchDTO();
        searchDTO.setCountry(country);
        searchDTO.setCommunityPostId(number);
        searchDTO.setTitle(title);
        searchDTO.setUserId(writer);
        searchDTO.setStartDate(startDate);
        searchDTO.setEndDate(endDate);
        searchDTO.setPage(page);
        searchDTO.setRecordSize(recordSize);

        return communityService.searchCommunityPosts(searchDTO);
    }
}
