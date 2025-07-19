package com.ruha.repository;

import com.ruha.entity.Follow;
import com.ruha.entity.Member;
import com.ruha.exception.follow.FollowNotFoundException;
import com.ruha.exception.member.MemberErrorCode;
import com.ruha.exception.member.MemberNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
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
    @DisplayName("팔로우 - 성공")
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
    @DisplayName("팔로우 조회 - 성공")
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
    @DisplayName("팔로우 조회 - 실패, 존재하지 않는 회원")
    void 팔로우_조회_존재하지_않는_회원() {

        // given
        Member member = Member.builder()
                .memberId(999L)
                .build();

        // when
        Optional<Follow> foundFollow = followRepository.findByFollowerAndFollowing(follower, member);

        // then
        assertThat(foundFollow).isEmpty();
    }

    @Test
    @DisplayName("팔로우 조회 - 실패, 존재하지 않는 관계")
    void 팔로우_조회_실패() {

        // given

        // when
        Optional<Follow> foundFollow = followRepository.findByFollowerAndFollowing(follower, following);

        // then
        assertThat(foundFollow).isEmpty();
    }

    @Test
    @DisplayName("언팔로우 - 성공")
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

    @Test
    @DisplayName("팔로잉 목록 조회")
    void 팔로잉_목록_조회() {

        // given
        Member following2 = Member.builder()
                .email("test3@example.com")
                .password("1234")
                .name("test3")
                .build();

        memberRepository.save(following2);

        Follow follow1 = Follow.builder()
                .follower(follower)
                .following(following)
                .build();

        Follow follow2 = Follow.builder()
                .follower(follower)
                .following(following2)
                .build();

        followRepository.save(follow1);
        followRepository.save(follow2);

        // when
        List<Follow> followings = followRepository.findByFollower(follower);

        // then
        assertThat(followings).hasSize(2);
        assertThat(followings).extracting(Follow::getFollowing).containsExactlyInAnyOrder(following, following2);

    }


    @Test
    @DisplayName("팔로워 목록 조회")
    void 팔로워_목록_조회() {
        // given
        Member following2 = Member.builder().email("test4@example.com").password("1234").name("test4").build();
        memberRepository.save(following2); //

        Follow follow1 = Follow.builder().follower(follower).following(following).build();
        Follow follow2 = Follow.builder().follower(following2).following(following).build();
        followRepository.save(follow1);
        followRepository.save(follow2);

        // when
        List<Follow> followers = followRepository.findByFollowing(following);

        // then
        assertThat(followers).hasSize(2);
        assertThat(followers).extracting(Follow::getFollower).containsExactlyInAnyOrder(follower, following2);
    }

    @Test
    @DisplayName("팔로잉 목록 조회 - 결과 없음")
    void findByFollower_결과_없음() {
        // when
        List<Follow> followings = followRepository.findByFollower(follower);

        // then
        assertThat(followings).isEmpty();
    }

    @Test
    @DisplayName("팔로워 목록 조회 - 결과 없음")
    void findByFollowing_결과_없음() {

        // when
        List<Follow> followers = followRepository.findByFollowing(following);

        // then
        assertThat(followers).isEmpty();
    }
}