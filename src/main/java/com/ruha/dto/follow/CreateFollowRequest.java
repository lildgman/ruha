package com.ruha.dto.follow;

import lombok.Data;

@Data
public class CreateFollowRequest {

    private Long followerId;
    private Long followingId;

}
