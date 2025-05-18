package com.project.Journey.login.follow.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FollowRequestDTO {
    private String targetLoginId; //팔로우할 대상 사용자의 로그인 아이디

    //private Long targetId; // 팔로우할 대상 사용자 ID
}
