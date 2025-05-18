package com.project.Journey.login.member.dto;

import com.project.Journey.login.member.domain.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Schema(description = "프로필 조회 응답 DTO")
@Getter @Builder
public class ProfileResponse {
    @Schema(description = "회원 PK", example = "7")
    private Long memberId;

    @Schema(description = "로그인 ID", example = "testUser")
    private String loginId;

    @Schema(description = "닉네임", example = "모아찌")
    private String nickname;

    @Schema(description = "나이", example = "27")
    private Integer age;

    @Schema(description = "성별", example = "MALE")
    private Gender gender;

    @Schema(description = "지역", example = "경기도 성남시")
    private String region;

    @Schema(description = "홈페이지/SNS", example = "https://instagram.com/moAzzi")
    private String homepage;

    @Schema(description = "소개글", example = "캠핑·사진 좋아해요")
    private String bio;

    @Schema(description = "프로필 이미지 URL")
    private String profileImage;

    @Schema(description = "태그(최대 3개)")
    private List<String> tags;
}
