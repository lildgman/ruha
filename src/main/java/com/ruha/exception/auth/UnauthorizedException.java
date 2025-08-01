package com.ruha.exception.auth;

import com.ruha.exception.CustomException;

public class UnauthorizedException extends CustomException {
    
    public UnauthorizedException() {
        super(AuthErrorCode.UNAUTHORIZED);
    }
}