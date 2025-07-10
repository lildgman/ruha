package com.ruha.exception;

import lombok.Getter;

@Getter
public class DuplicateFollowException extends RuntimeException{

    private final FollowErrorCode followErrorCode;

    public DuplicateFollowException() {
        super(FollowErrorCode.SELF_FOLLOW_NOT_ALLOWED.getMessage());
        this.followErrorCode = FollowErrorCode.SELF_FOLLOW_NOT_ALLOWED;
    }

    public DuplicateFollowException(String message) {
        super(message);
        this.followErrorCode = FollowErrorCode.SELF_FOLLOW_NOT_ALLOWED;
    }

    public DuplicateFollowException(FollowErrorCode followErrorCode) {
        super(followErrorCode.getMessage());
        this.followErrorCode = followErrorCode;
    }

}
