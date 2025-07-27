package com.ruha.exception.follow;

import com.ruha.exception.CustomException;

public class FollowNotFoundException extends CustomException {

    public FollowNotFoundException() {
        super(FollowErrorCode.FOLLOW_NOT_FOUND);
    }
}
