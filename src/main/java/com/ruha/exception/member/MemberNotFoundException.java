package com.ruha.exception.member;

import lombok.Getter;

@Getter
public class MemberNotFoundException extends RuntimeException {

    private final MemberErrorCode memberErrorCode;

    public MemberNotFoundException() {
        super(MemberErrorCode.MEMBER_NOT_FOUND.getMessage());
        this.memberErrorCode = MemberErrorCode.MEMBER_NOT_FOUND;
    }

    public MemberNotFoundException(String message) {
        super(message);
        this.memberErrorCode = MemberErrorCode.MEMBER_NOT_FOUND;
    }

    public MemberNotFoundException(MemberErrorCode memberErrorCode) {
        super(memberErrorCode.getMessage());
        this.memberErrorCode = memberErrorCode;
    }
}
