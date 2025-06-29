package com.project.Journey.login.member.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.Journey.login.member.domain.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Schema(description = "프로필 수정 요청 DTO")
@Setter @Getter
public class ProfileUpdateRequestDTO {

    @Schema(description = "닉네임(선택)", example = "모아찌")
    private String nickname;

    @Schema(description = "나이(선택)", example = "29")
    @Min(1) @Max(120)
    private Integer age;

    @Schema(description = "성별(선택)", example = "FEMALE")
    private Gender gender;

    @Schema(description = "지역(선택)", example = "서울특별시 강남구")
    @Size(max = 50)
    private String region;

    @Schema(description = "홈페이지/SNS 링크(선택)")
    @Size(max = 255)
    private String homepage;

    @Schema(description = "소개글(선택)")
    @Size(max = 500)
    private String bio;

    @Schema(description = "태그 목록(최대 3개, 각 6자 이하)", example = "[\"캠핑좋아\",\"사진기사임\"]")
    @Size(max = 3, message = "태그는 최대 3개까지만 저장할 수 있습니다")
    private List<@Size(max = 6, message = "태그 한 글자는 6자 이하") String> tags;

    @Schema(description = "프로필 이미지 URL 또는 null", example = "https://.../user.png")
    @JsonProperty("profile_image")
    private String profileImage;
}
