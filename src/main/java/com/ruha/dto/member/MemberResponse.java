package com.ruha.dto.member;

import com.ruha.entity.Member;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberResponse {

    private final Long memberId;
    private final String nickname;
    private final String name;
    private final LocalDateTime createdAt;
    private final long followerCount;
    private final long followingCount;
    private final long commentCount;
    private final long totalTodoCount;
    private final long completedTodoCount;
    private final double todoCompletionRate;


    public static MemberResponse from(Member member, long followerCount, long followingCount, long commentCount, long totalTodoCount, long completedTodoCount) {
        double todoCompletionRate = (totalTodoCount == 0) ? 0 : (double) completedTodoCount / totalTodoCount;
        return MemberResponse.builder()
                .memberId(member.getMemberId())
                .nickname(member.getNickname())
                .name(member.getName())
                .createdAt(member.getCreatedAt())
                .followerCount(followerCount)
                .followingCount(followingCount)
                .commentCount(commentCount)
                .totalTodoCount(totalTodoCount)
                .todoCompletionRate(todoCompletionRate)
                .build();
    }
}
