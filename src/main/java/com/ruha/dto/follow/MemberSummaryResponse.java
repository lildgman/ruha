package com.ruha.dto.follow;

import com.ruha.entity.Member;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberSummaryResponse {

    private final Long memberId;
    private final String nickname;
    private final String name;
    private final LocalDateTime createdAt;
    private final boolean isFollowing;

    public static MemberSummaryResponse of(Member member, boolean isFollowing) {

        return MemberSummaryResponse.builder()
                .memberId(member.getMemberId())
                .nickname(member.getNickname())
                .name(member.getName())
                .createdAt(member.getCreatedAt())
                .isFollowing(isFollowing)
                .build();
    }

    public static MemberSummaryResponse of(Member member) {

        return of(member, false);
    }



}
