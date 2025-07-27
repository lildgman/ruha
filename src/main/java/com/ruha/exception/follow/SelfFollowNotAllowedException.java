package com.ruha.exception.follow;

import com.ruha.exception.CustomException;

public class SelfFollowNotAllowedException extends CustomException {

    public SelfFollowNotAllowedException() {
        super(FollowErrorCode.SELF_FOLLOW_NOT_ALLOWED);
    }
}
