package com.project.Journey.story.service;

import com.project.Journey.awss3.S3Service;
import com.project.Journey.login.follow.entity.Follow;
import com.project.Journey.login.follow.repository.FollowRepository;
import com.project.Journey.login.member.domain.Member;
import com.project.Journey.login.member.repository.MemberRepository;
import com.project.Journey.story.dto.StoryRequestDTO;
import com.project.Journey.story.dto.StoryResponseDTO;
import com.project.Journey.story.entity.Story;
import com.project.Journey.story.repository.StoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StoryService {

    private final MemberRepository memberRepository;
    private final FollowRepository followRepository;
    private final StoryRepository storyRepository;
    private final S3Service s3Service;

    @Transactional
    public void uploadStory(String memberLoginId, MultipartFile imageFile, String expireAtStr){
        Member author = memberRepository.findByLoginId(memberLoginId)
                .orElseThrow(()-> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        //스토리 이미지 업로드
        String imageUrl = s3Service.uploadApplicationImage(imageFile);

        //스토리 만료 시간을 지정(지정해주지 않으면 24시간 후에 지워짐)
        LocalDateTime expireAt;
        if(expireAtStr != null){
            expireAt=LocalDateTime.parse(expireAtStr);
        }else{
            expireAt = LocalDateTime.now().plusHours(24);
        }


        Story story = new Story(author,imageUrl,expireAt);
        storyRepository.save(story);
    }

    //내가 팔로잉한 사람들의 활성 스토리를 최신순으로 최대 limit개 가져오기
    @Transactional(readOnly = true)
    public List<StoryResponseDTO> getRecentStoriesOfFollowing(String memberLoginId, int limit){
        Member me = memberRepository.findByLoginId(memberLoginId)
                .orElseThrow(()-> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        //팔로잉 목록 조회
        List<Follow> follows = followRepository.findByFollower(me);
        List<Member> followingMembers = new ArrayList<>();

        for(Follow f : follows){
            followingMembers.add(f.getFollowing());
        }

        if(followingMembers.isEmpty()){
            return Collections.emptyList();
        }

        Pageable pageable = PageRequest.of(0, limit);

        List<Story> stories = storyRepository
                .findByAuthorInAndExpireAtGreaterThanOrderByCreatedAtDesc(
                        followingMembers,
                        LocalDateTime.now(),
                        pageable
                );

        //DTO 변환
        List<StoryResponseDTO> responseDTOS = new ArrayList<>();
        for(Story s : stories){
            responseDTOS.add(new StoryResponseDTO(s));
        }

        return responseDTOS;
    }







    //내가 팔로잉한 사람들의 스토리 불러오기(최신순 x)
    @Transactional(readOnly = true)
    public List<StoryResponseDTO> getStoriesOfFollowing(String memberLoginId){
        Member me = memberRepository.findByLoginId(memberLoginId)
                .orElseThrow(()-> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        List<Follow> follows = followRepository.findByFollower(me);
        List<Member> followingMembers = new ArrayList<>();

        for(Follow follow : follows){
            followingMembers.add(follow.getFollowing());
        }

        List<Story> stories = storyRepository.findActiveByAuthors(followingMembers, LocalDateTime.now());

        List<StoryResponseDTO> result = new ArrayList<>();
        for(Story story : stories){
            result.add(new StoryResponseDTO(story));
        }
        return result;
    }


    //나의 스토리 불러오기
    @Transactional(readOnly = true)
    public List<StoryResponseDTO> getMyStories(String memberLoginId){
        Member me = memberRepository.findByLoginId(memberLoginId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        List<Story> myStories = storyRepository.findByAuthorOrderByCreatedAtDesc(me);

        List<StoryResponseDTO> responseDTOList = new ArrayList<>();
        for(Story story : myStories){
            responseDTOList.add(new StoryResponseDTO(story));
        }

        return responseDTOList;
    }


    //스토리 삭제
    @Transactional
    public void deleteStory(Long storyId, String memberLoginId){

        //1. 삭제할 스토리 조회
        Story story = storyRepository.findById(storyId)
                .orElseThrow(()-> new IllegalArgumentException("스토리를 찾을 수 없습니다."));

        //2. 작성자 본인인지 검증
        if(!story.getAuthor().getLoginId().equals(memberLoginId)){
            throw new SecurityException("본인이 작성한 스토리만 삭제할 수 있습니다.");
        }

        //3. 스토리 이미지 S3에서도 삭제
        String imageUrl = story.getImageUrl();
        if(imageUrl != null && !imageUrl.isEmpty()){
            s3Service.deleteS3Image(imageUrl);
        }

        //4. DB에서 스토리 삭제
        storyRepository.delete(story);
    }


    //스토리 ID로 특정 스토리 조회하기
    @Transactional(readOnly = true)
    public StoryResponseDTO getStoryById(Long storyId) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new IllegalArgumentException("스토리를 찾을 수 없습니다."));

        return new StoryResponseDTO(story);
    }

    //내가 마지막으로 올린(최근에 올린) 스토리 조회하기
    @Transactional(readOnly = true)
    public StoryResponseDTO getLatestStoryByMember(String memberLoginId) {
        Story story = storyRepository.findTop1ByAuthor_LoginIdOrderByCreatedAtDesc(memberLoginId)
                .orElseThrow(() -> new IllegalArgumentException("스토리가 존재하지 않습니다."));
        return new StoryResponseDTO(story);
    }

}
