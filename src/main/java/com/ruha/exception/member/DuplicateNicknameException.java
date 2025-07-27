package com.ruha.exception.member;

import com.ruha.exception.CustomException;

public class DuplicateNicknameException extends CustomException {

    public DuplicateNicknameException() {
        super(MemberErrorCode.DUPLICATE_NICKNAME);
    }
}
