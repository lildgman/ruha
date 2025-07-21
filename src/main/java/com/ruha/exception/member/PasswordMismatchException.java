package com.ruha.exception.member;

import lombok.Getter;

@Getter
public class PasswordMismatchException extends RuntimeException {

    private final MemberErrorCode errorCode;

    public PasswordMismatchException(MemberErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
