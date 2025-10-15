package com.ruha.dto.follow.request;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class FollowRequest {

    @NotNull(message = "팔로우할 회원 ID는 필수입니다.")
    private Long targetMemberId;

}
