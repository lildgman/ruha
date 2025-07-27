package com.ruha.exception.follow;

import com.ruha.exception.CustomException;

public class DuplicateFollowException extends CustomException {

    public DuplicateFollowException() {
        super(FollowErrorCode.DUPLICATE_FOLLOW);
    }
}
