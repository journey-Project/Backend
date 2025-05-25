package com.project.Journey.login.follow.controller;

import com.project.Journey.login.follow.dto.FollowRequestDTO;
import com.project.Journey.login.follow.dto.FollowResponseDTO;
import com.project.Journey.login.follow.service.FollwService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
    name = "팔로우 기능",
    description = "사용자 팔로우/언팔로우 및 목록 조회 관련 API")
@RestController
@RequiredArgsConstructor
public class FollowController {

    private final FollwService follwService;

    //팔로우
    //http://localhost:8080/api/follow?myLoginId=user1
    @Operation(
        summary = "팔로우 요청",
        description = """
            로그인한 사용자가 다른 사용자를 팔로우합니다.
            
            POST http://localhost:8080/api/follow?myLoginId=user1
                        
            request :\s
            {
              "targetLoginId": "user2"
            }
            
            -> user1이 user2를 팔로우 함
            
            """,
        parameters = {
            @Parameter(name = "myLoginId", description = "팔로우를 요청하는 자신의 로그인 ID", required = true, in = ParameterIn.QUERY)
        },
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "팔로우 대상 ID",
            required = true,
            content = @Content(schema = @Schema(implementation = FollowRequestDTO.class))
        ),
        responses = {
            @ApiResponse(responseCode = "200", description = "팔로우 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "404", description = "대상 사용자를 찾을 수 없음", content = @Content)
        }
    )
    @PostMapping("/api/follow")
    public ResponseEntity<Void> follow(@RequestParam String myLoginId,  @RequestBody FollowRequestDTO request){
        follwService.follow(myLoginId, request.getTargetLoginId());
        return ResponseEntity.ok().build();
    }

    //언팔로우
    //http://localhost:8080/api/unfollow?myLoginId=user3&targetId=user1
    @Operation(
        summary = "언팔로우 요청",
        description = """
            로그인한 사용자가 다른 사용자를 언팔로우합니다.
            
            DELETE http://localhost:8080/api/unfollow?myLoginId=user3&targetId=user1
                        
            -> user3가 user1을 언팔로우
            
            """,
        parameters = {
            @Parameter(name = "myLoginId", description = "언팔로우를 요청하는 자신의 로그인 ID", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "targetId", description = "언팔로우 대상 로그인 ID", required = true, in = ParameterIn.QUERY)
        },
        responses = {
            @ApiResponse(responseCode = "200", description = "언팔로우 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "404", description = "대상 사용자를 찾을 수 없음", content = @Content)
        }
    )
    @DeleteMapping("/api/unfollow")
    public ResponseEntity<Void> unfollow(@RequestParam String myLoginId, @RequestParam String targetId) {
        follwService.unfollow(myLoginId, targetId);
        return ResponseEntity.ok().build();
    }



    //팔로잉한 사람들 리스트 가져오기
    //http://localhost:8080/api/follow/following?memberLoginId=user1
    @Operation(
        summary = "팔로잉 목록 조회",
        description = """
            특정 사용자가 팔로잉 중인 사용자 목록을 반환합니다.
            
            GET http://localhost:8080/api/follow/following?memberLoginId=user1
                        
            -> user1이 팔로잉한 사람들 리스트 가져옵니다
            response 예시
            [
                {
                    "memberId": 1,
                    "username": "미국여행자",
                    "profileImageUrl": null
                },
                {
                    "memberId": 7,
                    "username": "프랑스여행자",
                    "profileImageUrl": null
                }
            .....
            ]
                        
            
            """,
        parameters = {
            @Parameter(name = "memberLoginId", description = "조회할 사용자의 로그인 ID", required = true, in = ParameterIn.QUERY)
        },
        responses = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                content = @Content(schema = @Schema(implementation = FollowResponseDTO.class))
            ),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content)
        }
    )
    @GetMapping("/api/follow/following")
    public List<FollowResponseDTO> getFollowing(@RequestParam String memberLoginId){
        return follwService.getFollowingList(memberLoginId);
    }

    //팔로우한 사람들 리스트 가져오기
    @Operation(
        summary = "팔로워 목록 조회",
        description = """
            특정 사용자를 팔로우 중인 사용자 목록을 반환합니다.
            
            GET http://localhost:8080/api/follow/followers?memberLoginId=user1
                        
            -> user1을 팔로우한 사람들 리스트 가져옵니다
            response 예시
            [
                {
                    "memberId": 7,
                    "username": "팔로워1",
                    "profileImageUrl": null
                },
                {
                    "memberId": 8,
                    "username": "팔로워2",
                    "profileImageUrl": null
                }
            ]
            
            """,
        parameters = {
            @Parameter(name = "memberLoginId", description = "조회할 사용자의 로그인 ID", required = true, in = ParameterIn.QUERY)
        },
        responses = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                content = @Content(schema = @Schema(implementation = FollowResponseDTO.class))
            ),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content)
        }
    )
    @GetMapping("/api/follow/followers")
    public List<FollowResponseDTO> getFollowers(@RequestParam String memberLoginId){
        return follwService.getFollowerList(memberLoginId);
    }
}
