package com.ruha.exception.member;

import com.ruha.exception.CustomException;

public class MemberNotFoundException extends CustomException {

    public MemberNotFoundException() {
        super(MemberErrorCode.MEMBER_NOT_FOUND);
    }
}
