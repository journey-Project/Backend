package com.project.Journey.login.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "닉네임과 프로필 이미지 전용 응답 DTO")
public class ProfileImageResponseDTO {

    @Schema(description = "회원 PK", example = "7")
    private Long memberId;

    @Schema(description = "닉네임(없으면 loginId)", example = "모아찌")
    private String nickname;

    @Schema(description = "프로필 이미지 URL(없으면 기본이미지)", example = "https://our-s3-bucket.com/imgaes/profile7.png")
    private String profileImage;
}