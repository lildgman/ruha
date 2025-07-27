package com.ruha.exception.member;

import com.ruha.exception.CustomException;

public class PasswordMismatchException extends CustomException {

    public PasswordMismatchException() {
        super(MemberErrorCode.PASSWORD_MISMATCH);
    }
}
