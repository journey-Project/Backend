package com.project.Journey.login.member.service;

import com.project.Journey.awss3.S3Service;
import com.project.Journey.login.member.domain.Member;
import com.project.Journey.login.member.domain.MemberTag;
import com.project.Journey.login.member.dto.ProfileImageResponseDTO;
import com.project.Journey.login.member.dto.ProfileResponseDTO;
import com.project.Journey.login.member.dto.ProfileUpdateRequestDTO;
import com.project.Journey.login.member.repository.MemberRepository;
import com.project.Journey.login.member.repository.MemberTagRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
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

    public void updateProfile(Long memberId, ProfileUpdateRequestDTO dto) {
        Member m = memberRepo.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원 없음"));

        m.updateProfile(
                dto.getNickname(),
                dto.getAge(),
                dto.getGender(),
                dto.getRegion(),
                dto.getHomepage(),
                dto.getBio()
        );

        tagRepo.deleteByMember(m);
        if (dto.getTags() != null && !dto.getTags().isEmpty()) {

            if (dto.getTags().size() > 3)
                throw new IllegalArgumentException("태그는 최대 3개까지 가능합니다");

            dto.getTags().forEach(t -> {
                if (t.length() > 6)
                    throw new IllegalArgumentException("태그 '" + t + "' 는 6자를 초과합니다");
                tagRepo.save(new MemberTag(m, t));
            });
        }
    }

    public ProfileImageResponseDTO getProfileImage(Long memberId) {
        Member member = memberRepo.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원 없음"));

        return ProfileImageResponseDTO.builder()
                .memberId(member.getId())
                .nickname(member.getDisplayName())
                .profileImage(member.getProfileImage())
                .build();
    }


    @Transactional
    public void updateProfileImage(Long memberId, MultipartFile file) {
        Member member = memberRepo.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("회원 없음"));

        // 기존 이미지 삭제 (선택)
        if (member.getProfileImage() != null) {
            s3Service.deleteS3Image(member.getProfileImage());
        }

        String imageUrl = s3Service.uploadProfileImage(file, member.getRole());
        member.setProfileImage(imageUrl);
    }

}
