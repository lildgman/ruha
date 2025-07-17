package com.ruha.exception.follow;

import lombok.Getter;

@Getter
public class FollowNotFoundException extends RuntimeException {

    private final FollowErrorCode followErrorCode;

    public FollowNotFoundException() {
        super(FollowErrorCode.FOLLOW_NOT_FOUND.getMessage());
        this.followErrorCode = FollowErrorCode.FOLLOW_NOT_FOUND;
    }

    public FollowNotFoundException(String message) {
        super(message);
        this.followErrorCode = FollowErrorCode.FOLLOW_NOT_FOUND;
    }

    public FollowNotFoundException(FollowErrorCode followErrorCode) {
        super(followErrorCode.getMessage());
        this.followErrorCode = followErrorCode;
    }
}
