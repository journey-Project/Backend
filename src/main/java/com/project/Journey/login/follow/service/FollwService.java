package com.project.Journey.login.follow.service;

import com.project.Journey.login.follow.dto.FollowResponseDTO;
import com.project.Journey.login.follow.entity.Follow;
import com.project.Journey.login.follow.repository.FollowRepository;
import com.project.Journey.login.member.domain.Member;
import com.project.Journey.login.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class FollwService {

    private final FollowRepository followRepository;
    private final MemberRepository memberRepository;

    //팔로우
    public void follow(String myLoginId, String targetLoginId){
        Member me = memberRepository.findByLoginId(myLoginId).orElseThrow(() -> new EntityNotFoundException("내 계정이 존재하지 않습니다: " + myLoginId));

        Member target = memberRepository.findByLoginId(targetLoginId).orElseThrow(() -> new EntityNotFoundException("팔로우 대상 계정이 존재하지 않습니다: " + targetLoginId));

        if(followRepository.existsByFollowerAndFollowing(me, target)){
            throw new IllegalStateException(me+"는 이미"+target+"을 팔로우하고 있습니다.");
        }

        Follow follow = new Follow();
        follow.setFollower(me);
        follow.setFollowing(target);
        followRepository.save(follow);

    }

    //언팔로우
    public void unfollow(String myLoginId, String targetLoginId){
        Member me = memberRepository.findByLoginId(myLoginId).orElseThrow(() -> new EntityNotFoundException("내 계정이 존재하지 않습니다: " + myLoginId));

        Member target = memberRepository.findByLoginId(targetLoginId)
                .orElseThrow(() -> new EntityNotFoundException("언팔로우 대상 계정이 존재하지 않습니다: " + targetLoginId));

        Follow follow = followRepository.findByFollowerAndFollowing(me, target)
                .orElseThrow(()-> new IllegalStateException("팔로우 관계가 존재하지 않습니다."));

        followRepository.delete(follow);
    }

    //팔로잉 리스트 가져오기
    public List<FollowResponseDTO> getFollowingList(String LoginId){
        Member member = memberRepository.findByLoginId(LoginId).orElseThrow(() -> new EntityNotFoundException(LoginId+"의 계정이 존재하지 않습니다."));

        List<Follow> follows = followRepository.findByFollower(member);
        List<FollowResponseDTO> result = new ArrayList<>();

        for(Follow follow : follows){
            Member following = follow.getFollowing();
            FollowResponseDTO followResponseDTO = new FollowResponseDTO(following);
            result.add(followResponseDTO);
        }

        return result;
    }

    //팔로워 리스트 가져오기
    public List<FollowResponseDTO> getFollowerList(String LoginId){
        Member member = memberRepository.findByLoginId(LoginId).orElseThrow(() -> new EntityNotFoundException(LoginId+"의 계정이 존재하지 않습니다."));
        List<Follow> follows = followRepository.findByFollowing(member);

        List<FollowResponseDTO> result = new ArrayList<>();
        for(Follow follow : follows){
            Member follower = follow.getFollower();
            FollowResponseDTO followResponseDTO = new FollowResponseDTO(follower);
            result.add(followResponseDTO);
        }
        return result;
    }


    //member id로 가져오기
    private Member getMember(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));
    }
}
