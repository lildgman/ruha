package com.ruha.exception.member;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode {
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND,"회원이 존재하지 않습니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT,"이미 사용중인 이메일입니다.");

    private final HttpStatus status;
    private final String message;

}
