package com.ruha.dto.follow;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 팔로워/팔로잉 목록 응답 DTO (총 개수 포함)
 */
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FollowListResponse {

    private final int totalCount;
    private final List<FollowResponse> follows;

    public static FollowListResponse of(List<FollowResponse> follows) {
        return FollowListResponse.builder()
                .totalCount(follows.size())
                .follows(follows)
                .build();
    }
}
