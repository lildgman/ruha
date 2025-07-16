package com.ruha.repository;

import com.ruha.entity.Follow;
import com.ruha.entity.Member;
import com.ruha.exception.FollowNotFoundException;
import com.ruha.exception.MemberErrorCode;
import com.ruha.exception.MemberNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class FollowRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private FollowRepository followRepository;

    private Member follower;
    private Member following;

    @BeforeEach
    void memberSetUp() {
        Member memberA = Member.builder()
                .email("test1@example.com")
                .password("1234")
                .name("test1")
                .build();

        Member memberB = Member.builder()
                .email("test2@example.com")
                .password("1234")
                .name("test2")
                .build();

        memberRepository.save(memberA);
        memberRepository.save(memberB);

        follower = memberRepository.findById(memberA.getMemberId())
                .orElseThrow(() -> new MemberNotFoundException(MemberErrorCode.MEMBER_NOT_FOUND));

        following = memberRepository.findById(memberB.getMemberId())
                .orElseThrow(() -> new MemberNotFoundException(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    @Test
    void 팔로우() {
        // given
        Follow follow = Follow.builder()
                .follower(follower)
                .following(following)
                .build();

        followRepository.save(follow);

        // when
        Follow foundFollow = followRepository.findById(follow.getFollowId())
                .orElseThrow(() -> new FollowNotFoundException("해당 팔로우 관계가 존재하지 않습니다."));

        // then
        assertThat(foundFollow.getFollowId()).isEqualTo(follow.getFollowId());
        assertThat(foundFollow.getFollower()).isEqualTo(follower);
        assertThat(foundFollow.getFollowing()).isEqualTo(following);
    }

    @Test
    void 팔로우_조회() {
        // given
        Follow follow = Follow.builder()
                .follower(follower)
                .following(following)
                .build();

        followRepository.save(follow);

        // when
        Follow foundFollow = followRepository.findByFollowerAndFollowing(follower, following)
                .orElseThrow(() -> new FollowNotFoundException("두 회원간의 팔로우 관계가 존재하지 않습니다."));

        // then
        assertThat(foundFollow.getFollower()).isEqualTo(follower);
        assertThat(foundFollow.getFollowing()).isEqualTo(following);
    }

    @Test
    void 언팔로우() {
        // given
        Follow follow = Follow.builder()
                .follower(follower)
                .following(following)
                .build();

        followRepository.save(follow);

        // when
        followRepository.delete(follow);

        // then
        Optional<Follow> foundFollow = followRepository.findByFollowerAndFollowing(follower, following);
        assertThat(foundFollow).isEmpty();

    }

}