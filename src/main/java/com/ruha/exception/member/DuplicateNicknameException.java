package com.ruha.exception.member;

import lombok.Getter;

@Getter
public class DuplicateNicknameException extends RuntimeException {

    private final MemberErrorCode errorCode;

    public DuplicateNicknameException() {
        super(MemberErrorCode.DUPLICATE_NICKNAME.getMessage());
        this.errorCode = MemberErrorCode.DUPLICATE_NICKNAME;
    }

    public DuplicateNicknameException(String message) {
        super(message);
        this.errorCode = MemberErrorCode.DUPLICATE_NICKNAME;
    }

    public DuplicateNicknameException(MemberErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

}
