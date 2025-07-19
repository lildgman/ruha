package com.ruha.service;

import com.ruha.entity.Follow;
import com.ruha.entity.Member;
import com.ruha.exception.follow.DuplicateFollowException;
import com.ruha.exception.follow.FollowErrorCode;
import com.ruha.exception.follow.FollowNotFoundException;
import com.ruha.exception.follow.SelfFollowNotAllowedException;
import com.ruha.repository.FollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.ruha.exception.follow.FollowErrorCode.FOLLOW_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FollowService {

    private final FollowRepository followRepository;

    @Transactional
    public void follow(Member fromMember, Member toMember) {
        if(fromMember.getMemberId().equals(toMember.getMemberId())) {
            throw new SelfFollowNotAllowedException(FollowErrorCode.SELF_FOLLOW_NOT_ALLOWED);
        }

        if (followRepository.existsByFollowerAndFollowing(fromMember, toMember)) {
            throw new DuplicateFollowException(FollowErrorCode.DUPLICATE_FOLLOW);
        }

        Follow follow = Follow.builder()
                .follower(fromMember)
                .following(toMember)
                .build();

        followRepository.save(follow);
    }

    @Transactional
    public void unfollow(Member fromMember, Member toMember) {
        Follow follow = followRepository.findByFollowerAndFollowing(fromMember, toMember)
                .orElseThrow(() -> new FollowNotFoundException(FOLLOW_NOT_FOUND));
        followRepository.delete(follow);
    }
}