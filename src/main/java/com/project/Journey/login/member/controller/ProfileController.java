package com.project.Journey.login.member.controller;

import com.project.Journey.login.member.dto.*;
import com.project.Journey.login.member.service.ProfileService;
import com.project.Journey.login.member.service.TravelPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "프로필 및 여행일정", description = "프로필·여행일정 관리 API")
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;
    private final TravelPlanService planService;

    @Operation(summary = "내 프로필 조회")
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = ProfileResponseDTO.class)))
    @GetMapping("/{id}/profile")
    public ProfileResponseDTO getProfile(@PathVariable Long id) {
        return profileService.getMyProfile(id);
    }

    @Operation(summary = "내 프로필 수정",
            description = "전달된 필드만 업데이트합니다.<br>· tags: 최대 3개, 각 6자 이하")
    @PatchMapping("/{id}/profile")
    public ResponseEntity<ProfileResponseDTO> updateProfile(
            @PathVariable("id") Long id,
            @Valid @RequestBody ProfileUpdateRequestDTO dto) {
        return ResponseEntity.ok(profileService.updateProfile(id, dto));
    }

    @Operation(summary = "여행 일정 추가")
    @ApiResponse(responseCode = "200", description = "등록 성공",
            content = @Content(schema = @Schema(implementation = TravelPlanResponseDTO.class)))
    @PostMapping("/{id}/plans")
    public TravelPlanResponseDTO addPlan(@PathVariable Long id,
                                         @RequestBody TravelPlanRequestDTO req) {
        return planService.addPlan(id, req);
    }

    @Operation(summary = "여행 일정 목록")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/{id}/plans")
    public List<TravelPlanResponseDTO> listPlans(@PathVariable Long id) {
        return planService.listPlans(id);
    }

    @Operation(summary = "여행 일정 수정")
    @ApiResponse(responseCode = "200", description = "수정 성공")
    @PatchMapping("/plans/{planId}")
    public TravelPlanResponseDTO updatePlan(@PathVariable Long planId,
                                            @RequestBody TravelPlanRequestDTO req) {
        return planService.updatePlan(planId, req);
    }

    @Operation(summary = "여행 일정 삭제")
    @ApiResponse(responseCode = "200", description = "삭제 성공")
    @DeleteMapping("/plans/{planId}")
    public ResponseEntity<?> deletePlan(@PathVariable Long planId) {
        planService.deletePlan(planId);
        return ResponseEntity.ok("삭제 완료");
    }

    @Operation(summary = "프로필 이미지와 닉네임 조회",
            description = "댓글, 목록, 헤더 등에서 사용할 가벼운 전용 API")
    @GetMapping("/{id}/profile-image")
    public ProfileImageResponseDTO getProfileImage(@PathVariable Long id) {
        return profileService.getProfileImage(id);
    }

    @Operation(summary = "프로필 이미지 업로드")
    @PostMapping(
            path = "/{id}/profile-image",
            consumes = "multipart/form-data")
    public ResponseEntity<ProfileImageResponseDTO> uploadProfileImage(
            @PathVariable Long id,
            @RequestParam("image") MultipartFile file) {

        ProfileImageResponseDTO res = profileService.updateProfileImage(id, file);
        return ResponseEntity.ok(res);           // 최신 URL 즉시 반환
    }

    @DeleteMapping("/members/{id}/profile-image")
    public ResponseEntity<?> deleteProfileImage(@PathVariable Long id) {
        profileService.deleteProfileImage(id);
        return ResponseEntity.noContent().build();
    }
}
