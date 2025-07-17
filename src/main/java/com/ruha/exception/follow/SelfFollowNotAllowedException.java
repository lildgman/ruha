package com.ruha.exception.follow;

import lombok.Getter;

@Getter
public class SelfFollowNotAllowedException extends RuntimeException{

    private final FollowErrorCode followErrorCode;

    public SelfFollowNotAllowedException() {
        super(FollowErrorCode.SELF_FOLLOW_NOT_ALLOWED.getMessage());
        this.followErrorCode = FollowErrorCode.SELF_FOLLOW_NOT_ALLOWED;
    }

    public SelfFollowNotAllowedException(String message) {
        super(message);
        this.followErrorCode = FollowErrorCode.SELF_FOLLOW_NOT_ALLOWED;
    }

    public SelfFollowNotAllowedException(FollowErrorCode followErrorCode) {
        super(followErrorCode.getMessage());
        this.followErrorCode = followErrorCode;
    }

}
