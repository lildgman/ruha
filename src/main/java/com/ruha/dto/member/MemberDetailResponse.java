package com.ruha.dto.member;

import com.ruha.entity.Member;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 특정 회원의 상세 정보를 담는 응답 DTO입니다.
 * MemberResponse와 달리 팔로우 여부 정보를 포함합니다.
 */
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberDetailResponse {

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
    private final Boolean isFollowing;

    /**
     * 비로그인 사용자를 위한 응답을 생성합니다. (팔로우 여부 null)
     *
     * @param member 조회 대상 회원
     * @param followerCount 팔로워 수
     * @param followingCount 팔로잉 수
     * @param commentCount 댓글 수
     * @param totalTodoCount 전체 공개 Todo 수
     * @param completedTodoCount 완료된 공개 Todo 수
     * @return 회원 상세 정보
     */
    public static MemberDetailResponse from(Member member, long followerCount, long followingCount, long commentCount, long totalTodoCount, long completedTodoCount) {
        return from(member, followerCount, followingCount, commentCount, totalTodoCount, completedTodoCount, null);
    }

    /**
     * 로그인 사용자를 위한 응답을 생성합니다. (팔로우 여부 포함)
     *
     * @param member 조회 대상 회원
     * @param followerCount 팔로워 수
     * @param followingCount 팔로잉 수
     * @param commentCount 댓글 수
     * @param totalTodoCount 전체 공개 Todo 수
     * @param completedTodoCount 완료된 공개 Todo 수
     * @param isFollowing 현재 로그인 사용자가 해당 회원을 팔로우하는지 여부
     * @return 회원 상세 정보
     */
    public static MemberDetailResponse from(Member member, long followerCount, long followingCount, long commentCount, long totalTodoCount, long completedTodoCount, Boolean isFollowing) {
        double todoCompletionRate = (totalTodoCount == 0) ? 0 : (double) completedTodoCount / totalTodoCount;
        return MemberDetailResponse.builder()
                .memberId(member.getMemberId())
                .nickname(member.getNickname())
                .name(member.getName())
                .createdAt(member.getCreatedAt())
                .followerCount(followerCount)
                .followingCount(followingCount)
                .commentCount(commentCount)
                .totalTodoCount(totalTodoCount)
                .completedTodoCount(completedTodoCount)
                .todoCompletionRate(todoCompletionRate)
                .isFollowing(isFollowing)
                .build();
    }

}
