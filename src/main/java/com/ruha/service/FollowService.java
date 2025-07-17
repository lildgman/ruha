package com.ruha.service;

import com.ruha.entity.Member;
import com.ruha.exception.follow.DuplicateFollowException;
import com.ruha.exception.follow.FollowErrorCode;
import com.ruha.exception.follow.SelfFollowNotAllowedException;
import com.ruha.exception.member.MemberErrorCode;
import com.ruha.exception.member.MemberNotFoundException;
import com.ruha.repository.FollowRepository;
import com.ruha.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final MemberRepository memberRepository;

    /**
     * 팔로우
     * @param followerId 팔로우를 요청한 회원 ID
     * @param followingId 팔로우 대상 회원 ID
     */
    public void follow(Long followerId, Long followingId) {

        // 자기 자신을 팔로우 할 시 예외 발생
        if (followerId.equals(followingId)) {
            throw new SelfFollowNotAllowedException(FollowErrorCode.SELF_FOLLOW_NOT_ALLOWED);
        }

        // 팔로워 회원
        Member follower = memberRepository.findById(followerId)
                .orElseThrow(() -> new MemberNotFoundException(MemberErrorCode.MEMBER_NOT_FOUND));

        // 팔로잉 회원
        Member following = memberRepository.findById(followingId)
                .orElseThrow(() -> new MemberNotFoundException(MemberErrorCode.MEMBER_NOT_FOUND));

        // 이미 팔로우 관계면 예외 발생
        if (followRepository.existsByFollowerAndFollowing(follower, following)) {
            throw new DuplicateFollowException(FollowErrorCode.DUPLICATE_FOLLOW);
        }

        follower.follow(following);

    }

    /**
     * 언팔로우
     * @param followerId 언팔로우를 요청한 회원ID
     * @param followingId 언팔로우 대상의 회원ID
     */
    public void unfollow(Long followerId, Long followingId) {
        // 팔로워 회원
        Member follower = memberRepository.findById(followerId)
                .orElseThrow(() -> new MemberNotFoundException(MemberErrorCode.MEMBER_NOT_FOUND));

        // 팔로잉 회원
        Member following = memberRepository.findById(followingId)
                .orElseThrow(() -> new MemberNotFoundException(MemberErrorCode.MEMBER_NOT_FOUND));

        follower.unfollow(following);

    }
}
