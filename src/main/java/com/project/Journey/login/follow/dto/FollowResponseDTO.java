package com.project.Journey.login.follow.dto;

import com.project.Journey.login.member.domain.Member;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FollowResponseDTO {

    private Long memberId;
    private String username;

    private String profileImageUrl;


    public FollowResponseDTO(Member member){
        this.memberId = member.getId();
        this.username = member.getLoginId();
        this.profileImageUrl = member.getProfileImage();
    }
}
