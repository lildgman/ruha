package com.ruha.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FollowErrorCode {
    FOLLOW_NOT_FOUND("FOLLOW_NOT_FOUND", "팔로우 정보가 존재하지 않습니다.");

    private final String code;
    private final String message;

}
