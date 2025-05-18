package com.project.Journey.story.controller;

import com.project.Journey.story.dto.StoryRequestDTO;
import com.project.Journey.story.dto.StoryResponseDTO;
import com.project.Journey.story.service.StoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/story")
public class StoryController {

    private final StoryService storyService;


    //스토리 업로드
    @PostMapping(
    consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> uploadStory(
            @RequestParam String memberLoginId,
            @RequestPart MultipartFile imageFile,
           @RequestParam(required = false) String expireAt
            ){
        storyService.uploadStory(memberLoginId, imageFile, expireAt);
        return ResponseEntity.status(201).build();
    }

    //내가 팔로잉한 사람들의 최신 스토리 최대 N개 조회
    //http://localhost:8080/api/story/following/recent?memberLoginId=user1&limit=4
    @GetMapping("/following/recent")
    public List<StoryResponseDTO> getRecentFollowingStories(
            @RequestParam String memberLoginId,
            @RequestParam(defaultValue = "4") int limit
    ){
        return storyService.getRecentStoriesOfFollowing(memberLoginId, limit);
    }

    //나의 스토리 보기
    //http://localhost:8080/api/story/me?memberLoginId=user1
    @GetMapping("/me")
    public List<StoryResponseDTO> getMyStories(@RequestParam String memberLoginId){
        return storyService.getMyStories(memberLoginId);
    }

    //사용자가 팔로잉한 사람들의 스토리 보기
    public List<StoryResponseDTO> getFollowingStories(@RequestParam String memberLoginId){
        return storyService.getStoriesOfFollowing(memberLoginId);
    }

    //스토리 삭제
    //DELETE http://localhost:8080/api/story/1
    //Key = memberLoginId, value = loginId 값
    //key = storyId, value = 스토리 ID
    @DeleteMapping("/{storyId}")
    public ResponseEntity<?> deleteMyStory(
            @PathVariable Long storyId,
            @RequestParam String memberLoginId
    ) {
        storyService.deleteStory(storyId, memberLoginId);
        return ResponseEntity.noContent().build();
    }

    //storyId로 스토리 조회
    //http://localhost:8080/api/story/1
    @GetMapping("/{storyId}")
    public StoryResponseDTO getStory(@PathVariable Long storyId) {
        return storyService.getStoryById(storyId);
    }

    //내가 마지막으로 올린(최근에 올린) 스토리 조회
    //http://localhost:8080/api/story/my-latest?memberLoginId=user1 -> user1이 가장 최근에 올린 이미지 반환
    /*
    response 예시
 {
    "imageUrl": "스토리이미지.jpg",
    "authorId": 1,
    "authorUsername": "user1",
    "authorProfileImageUrl": null,
    "createdAt": "2025-05-18T17:38:31.14527",
    "expireAt": "2025-05-19T17:38:31.14527"
}
    * */


    @GetMapping("/my-latest")
    public StoryResponseDTO getMyLatestStory(@RequestParam String memberLoginId) {
        System.out.println("memberLoginId = " + memberLoginId);
        return storyService.getLatestStoryByMember(memberLoginId);
    }

}
