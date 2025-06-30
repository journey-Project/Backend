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

    private static final String DEFAULT_PROFILE_IMAGE_URL =
            "https://journeybucket0.s3.ap-northeast-2.amazonaws.com/USER/5c380987-c103-4ed5-ae55-0baef59574b7.jpeg";

    private boolean isDefaultImage(String url) {
        return url == null || url.isBlank() || url.equals(DEFAULT_PROFILE_IMAGE_URL);
    }

    private String resolveProfileImage(String imageUrl) {
        return (imageUrl == null || imageUrl.isBlank()) ? DEFAULT_PROFILE_IMAGE_URL : imageUrl;
    }

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
                .profileImage(resolveProfileImage(m.getProfileImage()))
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

        String imageUrl = dto.getProfileImage();
        if (imageUrl == null || imageUrl.isBlank()) {
            if (!isDefaultImage(m.getProfileImage())) {          // 추가
                s3Service.deleteS3Image(m.getProfileImage());
            }
            m.setProfileImage(DEFAULT_PROFILE_IMAGE_URL);
        } else {
            m.setProfileImage(imageUrl);
        }

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

        return ProfileImageResponseDTO.builder()
                .memberId(member.getId())
                .nickname(member.getDisplayName())
                .profileImage(resolveProfileImage(member.getProfileImage()))
                .build();
    }


    @Transactional
    public ProfileImageResponseDTO updateProfileImage(Long memberId,
                                                      MultipartFile file) {

        Member member = memberRepo.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("회원 없음"));

        // 파일이 없거나 비어 있으면 → 기본 이미지로
        if (file == null || file.isEmpty()) {
            if (!isDefaultImage(member.getProfileImage())) {     // 변경
                s3Service.deleteS3Image(member.getProfileImage());
            }

            // 기본 이미지로 리셋
            member.setProfileImage(DEFAULT_PROFILE_IMAGE_URL);
            syncPrincipalIfPresent(memberId, member);

            return ProfileImageResponseDTO.builder()
                    .memberId(member.getId())
                    .nickname(member.getDisplayName())
                    .profileImage(DEFAULT_PROFILE_IMAGE_URL)
                    .build();
        }

        // 기존 이미지 삭제
        if (!isDefaultImage(member.getProfileImage())) {         // 변경
            s3Service.deleteS3Image(member.getProfileImage());
        }
        String imageUrl = s3Service.uploadProfileImage(file, member.getRole());
        member.setProfileImage(imageUrl);

        /* 세션 principal 최신화 */
        syncPrincipalIfPresent(memberId, member);

        return ProfileImageResponseDTO.builder()
                .memberId(member.getId())
                .nickname(member.getDisplayName())
                .profileImage(member.getProfileImage())
                .build();
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

}
