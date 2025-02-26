package com.project.Journey.community.controller;


import com.project.Journey.board.dto.PostDTO;
import com.project.Journey.board.exception.PostException;
import com.project.Journey.board.service.PostService;
import com.project.Journey.community.dto.CommunityDTO;
import com.project.Journey.community.service.CommunityService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "커뮤니티 게시글 관리", description = "커뮤니티 관련 API")
public class CommunityController {
    private final CommunityService communityService;

    @PostMapping("api/community/save")
    public ResponseEntity<Long> createPost(@RequestBody @Parameter(description = "게시글 저장에 필요한 정보", required = true) CommunityDTO communityDTO) {
        try{
            Long savedPostId = communityService.saveCommunityPost(communityDTO);
            return ResponseEntity.ok(savedPostId);
        } catch (Exception e){
            throw new PostException("게시글 저장 중 오류가 발생했습니다.", HttpStatus.BAD_REQUEST);
        }

    }


    //페이지 네이션 적용된
    @GetMapping("/api/community/getPostsByPage")
    public Page<CommunityDTO> getCommunityPosts(@RequestParam String country, Pageable pageable) {
        return communityService.getPostsByCountry(country, pageable);
    }

    // 특정 게시글 조회 (조회수 증가 반영)
    @GetMapping("/api/community/getPostByPostId/{communityPostId}")
    public CommunityDTO getCommunityPost(@PathVariable Long communityPostId) {
        return communityService.getPostByCommunityPostId(communityPostId);
    }


}
