package com.project.Journey.login.follow.controller;

import com.project.Journey.login.follow.dto.FollowRequestDTO;
import com.project.Journey.login.follow.dto.FollowResponseDTO;
import com.project.Journey.login.follow.service.FollowService;

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

    private final FollowService followService;

    //팔로우
    //http://localhost:8080/api/follow?myMemberId=1
    @Operation(
        summary = "팔로우 요청",
        description = """
            로그인한 사용자가 다른 사용자를 팔로우합니다.
            
            POST http://localhost:8080/api/follow?myMemberId=1
                        
            request :\s
            {
              "targetMemberId": 2
            }
            
            -> memberId가 1인 사용자가 memberId가 2인 사용자를 팔로우 함
            
            """,
        parameters = {
            @Parameter(name = "myMemberId", description = "팔로우를 요청하는 자신의 memberID", required = true, in = ParameterIn.QUERY)
        },
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "팔로우 대상 memberId",
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
    public ResponseEntity<Void> follow(@RequestParam Long myMemberId,  @RequestBody FollowRequestDTO request){
        followService.follow(myMemberId, request.getTargetMemberId());
        return ResponseEntity.ok().build();
    }

    //언팔로우
    //http://localhost:8080/api/unfollow?myMemberId=1&targetMemberId=2
    @Operation(
        summary = "언팔로우 요청",
        description = """
            로그인한 사용자가 다른 사용자를 언팔로우합니다.
            
            DELETE http://localhost:8080/api/unfollow?myMemberId=1&targetMemberId=2
                        
            -> memberId가 1인 사용자가 memberId가 2인 사용자를 언팔로우
            
            """,
        parameters = {
            @Parameter(name = "myMemberId", description = "언팔로우를 요청하는 자신의 memberId", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "targetMemberId", description = "언팔로우 대상 memberId", required = true, in = ParameterIn.QUERY)
        },
        responses = {
            @ApiResponse(responseCode = "200", description = "언팔로우 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "404", description = "대상 사용자를 찾을 수 없음", content = @Content)
        }
    )
    @DeleteMapping("/api/unfollow")
    public ResponseEntity<Void> unfollow(@RequestParam Long myMemberId, @RequestParam Long targetMemberId) {
        followService.unfollow(myMemberId, targetMemberId);
        return ResponseEntity.ok().build();
    }



    //팔로잉한 사람들 리스트 가져오기
    //http://localhost:8080/api/follow/following?memberId=1
    @Operation(
        summary = "팔로잉 목록 조회",
        description = """
            특정 사용자가 팔로잉 중인 사용자 목록을 반환합니다.
            
            GET http://localhost:8080/api/follow/following?memberId=1
                        
            -> memberId가 1인 사용자가 팔로잉한 사람들 리스트 가져옵니다
            response 예시
            [
                {
                    "memberId": 2,
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
            @Parameter(name = "memberId", description = "조회할 사용자의 memberId", required = true, in = ParameterIn.QUERY)
        },
        responses = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                content = @Content(schema = @Schema(implementation = FollowResponseDTO.class))
            ),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content)
        }
    )
    @GetMapping("/api/follow/following")
    public List<FollowResponseDTO> getFollowing(@RequestParam Long memberId){
        return followService.getFollowingList(memberId);
    }

    //팔로우한 사람들 리스트 가져오기
    @Operation(
        summary = "팔로워 목록 조회",
        description = """
            특정 사용자를 팔로우 중인 사용자 목록을 반환합니다.
            
            GET http://localhost:8080/api/follow/followers?memberId=1
                        
            -> memberId가 1인 사용자를 팔로우한 사람들 리스트 가져옵니다
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
            @Parameter(name = "memberId", description = "조회할 사용자의 memberId", required = true, in = ParameterIn.QUERY)
        },
        responses = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                content = @Content(schema = @Schema(implementation = FollowResponseDTO.class))
            ),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content)
        }
    )
    @GetMapping("/api/follow/followers")
    public List<FollowResponseDTO> getFollowers(@RequestParam Long memberId){
        return followService.getFollowerList(memberId);
    }
}
