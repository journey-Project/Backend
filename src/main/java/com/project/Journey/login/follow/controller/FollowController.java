package com.project.Journey.login.follow.controller;

import com.project.Journey.login.follow.dto.FollowRequestDTO;
import com.project.Journey.login.follow.dto.FollowResponseDTO;
import com.project.Journey.login.follow.service.FollwService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FollowController {

    private final FollwService follwService;

    //팔로우
    //http://localhost:8080/api/follow?myLoginId=user1
    @PostMapping("/api/follow")
    public ResponseEntity<Void> follow(@RequestParam String myLoginId,  @RequestBody FollowRequestDTO request){
        follwService.follow(myLoginId, request.getTargetLoginId());
        return ResponseEntity.ok().build();
    }

    //언팔로우
    //http://localhost:8080/api/unfollow?myLoginId=user3&targetId=user1
    @DeleteMapping("/api/unfollow")
    public ResponseEntity<Void> unfollow(@RequestParam String myLoginId, @RequestParam String targetId) {
        follwService.unfollow(myLoginId, targetId);
        return ResponseEntity.ok().build();
    }

    //팔로잉한 사람들 리스트 가져오기
    //http://localhost:8080/api/follow/following?memberLoginId=user1
    @GetMapping("/api/follow/following")
    public List<FollowResponseDTO> getFollowing(@RequestParam String memberLoginId){
        return follwService.getFollowingList(memberLoginId);
    }

    //팔로우한 사람들 리스트 가져오기
    @GetMapping("/api/follow/followers")
    public List<FollowResponseDTO> getFollowers(@RequestParam String memberLoginId){
        return follwService.getFollowerList(memberLoginId);
    }
}
