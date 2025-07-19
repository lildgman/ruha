package com.ruha.exception.member;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class DuplicateEmailException extends RuntimeException {

    private final MemberErrorCode errorCode;

    public DuplicateEmailException() {
        super(MemberErrorCode.DUPLICATE_EMAIL.getMessage());
        this.errorCode = MemberErrorCode.DUPLICATE_EMAIL;
    }

    public DuplicateEmailException(String message) {
        super(message);
        this.errorCode = MemberErrorCode.DUPLICATE_EMAIL;
    }

    public DuplicateEmailException(MemberErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

}
