package com.ruha.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    MEMBER_NOT_FOUND("MEMBER_NOT_FOUND","회원이 존재하지 않습니다.");

    private final String code;
    private final String message;

}
