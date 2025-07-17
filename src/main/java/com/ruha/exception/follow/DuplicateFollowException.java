package com.ruha.exception.follow;

import lombok.Getter;

@Getter
public class DuplicateFollowException extends RuntimeException{

    private final FollowErrorCode followErrorCode;

    public DuplicateFollowException() {
        super(FollowErrorCode.DUPLICATE_FOLLOW.getMessage());
        this.followErrorCode = FollowErrorCode.DUPLICATE_FOLLOW;
    }

    public DuplicateFollowException(String message) {
        super(message);
        this.followErrorCode = FollowErrorCode.DUPLICATE_FOLLOW;
    }

    public DuplicateFollowException(FollowErrorCode followErrorCode) {
        super(followErrorCode.getMessage());
        this.followErrorCode = followErrorCode;
    }

}
