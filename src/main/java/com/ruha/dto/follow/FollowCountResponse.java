package com.ruha.dto.follow;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FollowCountResponse {

    private final long followingCount;
    private final long followerCount;

}
