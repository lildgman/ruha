package com.ruha.dto.follow;

import com.ruha.entity.Follow;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FollowResponse {

    private final Long followId;
    private final Long followerId;
    private final String followerNickname;
    private final String followerName;
    private final Long followingId;
    private final String followingNickname;
    private final String followingName;
    private final LocalDateTime createdAt;

    public static FollowResponse of(Follow follow) {
        return FollowResponse.builder()
                .followId(follow.getFollowId())
                .followerId(follow.getFollower().getMemberId())
                .followerNickname(follow.getFollower().getNickname())
                .followerName(follow.getFollower().getName())
                .followingId(follow.getFollowing().getMemberId())
                .followingNickname(follow.getFollowing().getNickname())
                .followingName(follow.getFollowing().getName())
                .createdAt(follow.getCreatedAt())
                .build();
    }
}