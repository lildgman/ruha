package com.ruha.exception.follow;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum FollowErrorCode {
    FOLLOW_NOT_FOUND(HttpStatus.NOT_FOUND, "팔로우 정보가 존재하지 않습니다."),
    DUPLICATE_FOLLOW(HttpStatus.CONFLICT,"이미 팔로우한 회원입니다."),
    SELF_FOLLOW_NOT_ALLOWED(HttpStatus.CONFLICT,"자기 자신을 팔로우 할 수 없습니다.");


    private final HttpStatus status;
    private final String message;

}
