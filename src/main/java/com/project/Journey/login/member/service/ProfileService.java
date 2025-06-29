package com.project.Journey.login.member.service;

import com.project.Journey.awss3.S3Service;
import com.project.Journey.login.auth.CustomUserDetails;
import com.project.Journey.login.member.domain.Member;
import com.project.Journey.login.member.domain.MemberTag;
import com.project.Journey.login.member.dto.ProfileImageResponseDTO;
import com.project.Journey.login.member.dto.ProfileResponseDTO;
import com.project.Journey.login.member.dto.ProfileUpdateRequestDTO;
import com.project.Journey.login.member.repository.MemberRepository;
import com.project.Journey.login.member.repository.MemberTagRepository;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service @RequiredArgsConstructor
@Transactional
public class ProfileService {

    private final MemberRepository memberRepo;
    private final MemberTagRepository tagRepo;
    private final S3Service s3Service;

    @Transactional(readOnly = true)
    public ProfileResponseDTO getMyProfile(Long memberId) {

        Member m = memberRepo.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원 없음"));

        return ProfileResponseDTO.builder()
                .memberId(m.getId())
                .loginId(m.getLoginId())
                .nickname(m.getNickname())
                .age(m.getAge())
                .gender(m.getGender())
                .region(m.getRegion())
                .homepage(m.getHomepage())
                .bio(m.getBio())
                .profileImage((m.getProfileImage()))
                .tags(tagRepo.findByMember(m)
                        .stream().map(MemberTag::getTag).toList())
                .build();

    }

    @Transactional
    public ProfileResponseDTO updateProfile(Long memberId,
                                            ProfileUpdateRequestDTO dto) {

        Member m = memberRepo.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원 없음"));

        m.updateProfile(dto.getNickname(), dto.getAge(), dto.getGender(),
                dto.getRegion(), dto.getHomepage(), dto.getBio());
        m.setProfileImage(dto.getProfileImage());

        tagRepo.deleteByMember(m);
        if (dto.getTags() != null && !dto.getTags().isEmpty()) {
            if (dto.getTags().size() > 3)
                throw new IllegalArgumentException("태그는 최대 3개까지 가능합니다");
            dto.getTags().forEach(tag -> tagRepo.save(new MemberTag(m, tag)));
        }

        /* 3) (선택) 세션 principal 최신화 */
        syncPrincipalIfPresent(memberId, m);   // 아래 유틸 참고

        /* 4) 최신 DTO 반환 */
        return toProfileDTO(m);
    }

    private void syncPrincipalIfPresent(Long memberId, Member updated) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()
                && auth.getPrincipal() instanceof CustomUserDetails cud
                && cud.getId().equals(memberId)) {

            cud.refreshFrom(updated);   // CustomUserDetails 에 구현
        }
    }


    public ProfileImageResponseDTO getProfileImage(Long memberId) {
        Member member = memberRepo.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원 없음"));

        String profileImage = member.getProfileImage();
        if (profileImage == null || profileImage.isBlank()) {
            profileImage = "https://journeybucket0.s3.ap-northeast-2.amazonaws.com/USER/0089e5c3-05c3-466b-8fd5-56c41f14acc9.png";
        }

        return ProfileImageResponseDTO.builder()
                .memberId(member.getId())
                .nickname(member.getDisplayName())
                .profileImage(profileImage)
                .build();
    }

    @Transactional
    public ProfileImageResponseDTO updateProfileImage(Long memberId, MultipartFile file) {

        Member member = memberRepo.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("회원 없음"));

        if (file == null || file.isEmpty()) {
            if (member.getProfileImage() != null) {
                s3Service.deleteS3Image(member.getProfileImage());
            }
//            member.setProfileImage(null); // DB는 null로 저장
            member.setProfileImage("https://journeybucket0.s3.ap-northeast-2.amazonaws.com/USER/0089e5c3-05c3-466b-8fd5-56c41f14acc9.png");

            // ✅ 응답에서 default 이미지 포함
            return buildProfileImageResponse(member);
        }

        if (member.getProfileImage() != null) {
            s3Service.deleteS3Image(member.getProfileImage());
        }

        String imageUrl = s3Service.uploadProfileImage(file, member.getRole());
        member.setProfileImage(imageUrl);

        return buildProfileImageResponse(member);
    }

    private ProfileResponseDTO toProfileDTO(Member m) {
        return ProfileResponseDTO.builder()
                .memberId(m.getId())
                .loginId(m.getLoginId())
                .nickname(m.getNickname())
                .age(m.getAge())
                .gender(m.getGender())
                .region(m.getRegion())
                .homepage(m.getHomepage())
                .bio(m.getBio())
                .profileImage(resolveProfileImage(m.getProfileImage()))
                .tags(tagRepo.findByMember(m)
                        .stream()
                        .map(MemberTag::getTag)
                        .toList())
                .build();
    }

    @Operation(summary = "프로필 이미지 삭제", description = "프로필 이미지를 삭제하고 DB에 null로 저장합니다.")
    @Transactional
    public void deleteProfileImage(Long memberId) {
        Member member = memberRepo.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("회원 없음"));

        if (member.getProfileImage() != null) {
            s3Service.deleteS3Image(member.getProfileImage());
            member.setProfileImage(null);
            syncPrincipalIfPresent(memberId, member);
        }
    }


    private ProfileImageResponseDTO buildProfileImageResponse(Member member) {
        String profileImage = member.getProfileImage();
        if (profileImage == null || profileImage.isBlank()) {
            profileImage = "https://journeybucket0.s3.ap-northeast-2.amazonaws.com/USER/0089e5c3-05c3-466b-8fd5-56c41f14acc9.png";
        }

        return ProfileImageResponseDTO.builder()
                .memberId(member.getId())
                .nickname(member.getDisplayName())
                .profileImage(profileImage)
                .build();
    }
    private String resolveProfileImage(String profileImage) {
        return (profileImage == null || profileImage.isBlank())
                ? "https://journeybucket0.s3.ap-northeast-2.amazonaws.com/USER/0089e5c3-05c3-466b-8fd5-56c41f14acc9.png"
                : profileImage;
    }
}
