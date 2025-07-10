package com.ruha.exception;

import lombok.Getter;

@Getter
public class SelfFollowNotAllowedException extends RuntimeException{

    private final FollowErrorCode followErrorCode;

    public SelfFollowNotAllowedException() {
        super(FollowErrorCode.DUPLICATE_FOLLOW.getMessage());
        this.followErrorCode = FollowErrorCode.DUPLICATE_FOLLOW;
    }

    public SelfFollowNotAllowedException(String message) {
        super(message);
        this.followErrorCode = FollowErrorCode.DUPLICATE_FOLLOW;
    }

    public SelfFollowNotAllowedException(FollowErrorCode followErrorCode) {
        super(followErrorCode.getMessage());
        this.followErrorCode = followErrorCode;
    }

}
