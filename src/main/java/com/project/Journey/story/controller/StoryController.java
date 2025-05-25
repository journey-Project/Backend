package com.project.Journey.story.controller;

import com.project.Journey.story.dto.StoryRequestDTO;
import com.project.Journey.story.dto.StoryResponseDTO;
import com.project.Journey.story.service.StoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Tag(
    name = "스토리 기능",
    description = "스토리 관련 API"
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/story")
public class StoryController {

    private final StoryService storyService;


    //스토리 업로드
    @Operation(summary = "스토리 업로드", description = """
        사용자가 이미지를 업로드하여 스토리를 생성합니다.
        
        POST http://localhost:8080/api/story\s
        request -> key : memberLoginId, value = member loginId 값(String)
        key : imageFile, value = MultipartFile
        
        """)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "스토리 업로드 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content)
    })
    @PostMapping(
    consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> uploadStory(
            @Parameter(description = "사용자 로그인 ID", example = "user1")
            @RequestParam String memberLoginId,

            @Parameter(description = "업로드할 이미지 파일")
            @RequestPart MultipartFile imageFile,

            @Parameter(description = "스토리 만료 시간 (yyyy-MM-dd'T'HH:mm:ss 형식)", example = "2025-05-26T10:00:00")
           @RequestParam(required = false) String expireAt
            ){
        storyService.uploadStory(memberLoginId, imageFile, expireAt);
        return ResponseEntity.status(201).build();
    }

    //내가 팔로잉한 사람들의 최신 스토리 최대 N개 조회
    //http://localhost:8080/api/story/following/recent?memberLoginId=user1&limit=4
    @Operation(summary = "팔로잉한 사람들의 최신 스토리 조회", description = """
        사용자가 팔로잉한 사람들의 최신 스토리를 최대 N개까지 조회합니다.

        GET http://localhost:8080/api/story/following/recent?memberLoginId=user1&limit=4   
        
        -> user1이 팔로잉하고 있는 사람들의 스토리 중 최신 4개를 가져옵니다
        
        
        response 예시 : [
        {
        "imageUrl": "스토리이미지1.jpg",
        "authorId": 7,
        "authorUsername": "user2",
        "authorProfileImageUrl": null,
        "createdAt": "2025-05-18T19:30:34.35525",
        "expireAt": "2025-05-19T19:30:34.35525"
        },
        ......
        ]
        
        
        imageUrl -> 스토리 이미지주소 : String
        authorId -> 작성자 ID (Member id) : String
        authorUsername -> 작성자 LoginId (Member LoginId) : String
        authorProfileImageUrl -> 작성자 프로필 이미지주소 : String
        createdAt -> 스토리 생성 시각 : LocalDateTime
        expireAt -> 스토리 자동 삭제 시각 : LocalDateTime
                
        *스토리 자동 삭제 시간은 기본적으로 24시간 이후로 지정
        
        
        """)
    @GetMapping("/following/recent")
    public List<StoryResponseDTO> getRecentFollowingStories(

            @Parameter(description = "사용자 로그인 ID", example = "user1")
            @RequestParam String memberLoginId,

            @Parameter(description = "최대 조회 개수", example = "4")
            @RequestParam(defaultValue = "4") int limit
    ){
        return storyService.getRecentStoriesOfFollowing(memberLoginId, limit);
    }

    //나의 스토리 보기
    //http://localhost:8080/api/story/me?memberLoginId=user1
    @Operation(summary = "내 스토리 목록 조회", description = """
        로그인한 사용자의 전체 스토리 목록을 조회합니다.
        
        * 나의 스토리 보기(스토리 리스트)
        GET http://localhost:8080/api/story/me?memberLoginId=user1
                
        -> user1이 올렸던 스토리들을 가져옵니다
        response 예시
        [
            {
                "imageUrl": "이미지주소.jpg",
                "authorId": 1,
                "authorUsername": "user1",
                "authorProfileImageUrl": null,
                "createdAt": "2025-05-18T20:06:45.439394",
                "expireAt": "2025-05-19T20:06:45.438379"
            },
            {
                "imageUrl": "이미지주소2.jpg",
                "authorId": 1,
                "authorUsername": "user1",
                "authorProfileImageUrl": null,
                "createdAt": "2025-05-18T19:28:33.901978",
                "expireAt": "2025-05-19T19:28:33.901978"
            }
        ....
        ]
                
        
        """)
    @GetMapping("/me")
    public List<StoryResponseDTO> getMyStories(
        @Parameter(description = "사용자 로그인 ID", example = "user1")
        @RequestParam String memberLoginId){
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
    @Operation(summary = "스토리 삭제", description = """
        사용자가 본인의 스토리를 삭제합니다.
        
        DELETE http://localhost:8080/api/story/{삭제하려고하는storyId}
        request -> Key = memberLoginId, value = loginId 값
        key = storyId, value = 스토리 ID
        
        """)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "스토리 삭제 성공"),
        @ApiResponse(responseCode = "404", description = "스토리를 찾을 수 없음", content = @Content)
    })
    @DeleteMapping("/{storyId}")
    public ResponseEntity<?> deleteMyStory(
            @Parameter(description = "삭제할 스토리의 ID", example = "1")
            @PathVariable Long storyId,

            @Parameter(description = "사용자 로그인 ID", example = "user1")
            @RequestParam String memberLoginId
    ) {
        storyService.deleteStory(storyId, memberLoginId);
        return ResponseEntity.noContent().build();
    }

    //storyId로 스토리 조회
    //http://localhost:8080/api/story/1
    @Operation(summary = "스토리 상세 조회", description = """
    스토리 ID로 특정 스토리를 조회합니다.
    
        GET http://localhost:8080/api/story/8
            
        -> storyId가 8인 스토리를 가져옵니다
        response 예시
        {
            "imageUrl": "storyId가8인스토리이미지.jpg",
            "authorId": 1,
            "authorUsername": "user1",
            "authorProfileImageUrl": null,
            "createdAt": "2025-05-18T20:06:45.439394",
            "expireAt": "2025-05-19T20:06:45.438379"
        }
    
    """)
    @GetMapping("/{storyId}")
    public StoryResponseDTO getStory(
        @Parameter(description = "조회할 스토리의 ID", example = "1")
        @PathVariable Long storyId) {
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

    @Operation(summary = "내가 마지막으로 올린 스토리 조회", description = """
    로그인한 사용자가 마지막으로 업로드한(가장 최근의) 스토리를 조회합니다.
    
        * 내가 올린 스토리 보기(가장 최신)
        GET http://localhost:8080/api/story/my-latest?memberLoginId=user1\s
            
        -> user1이 올린 스토리를 가져옵니다.
        response 예시
        {
            "imageUrl": "방금올린스토리이미지.jpg",
            "authorId": 1,
            "authorUsername": "user1",
            "authorProfileImageUrl": null,
            "createdAt": "2025-05-18T20:06:45.439394",
            "expireAt": "2025-05-19T20:06:45.438379"
        }
    """)
    @GetMapping("/my-latest")
    public StoryResponseDTO getMyLatestStory(
        @Parameter(description = "사용자 로그인 ID", example = "user1")
        @RequestParam String memberLoginId) {

        return storyService.getLatestStoryByMember(memberLoginId);
    }

}
