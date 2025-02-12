package com.ruha.repository;

import com.ruha.dto.CreateFollowRequest;
import com.ruha.dto.CreateMemberRequest;
import com.ruha.entity.Follow;
import com.ruha.entity.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class FollowRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private FollowRepository followRepository;

    private Member memberA;
    private Member memberB;

    @BeforeEach
    void memberSetUp() {
        CreateMemberRequest request = new CreateMemberRequest("test1@example.com", "1234", "test1");
        memberA = Member.toEntity(request);

        request = new CreateMemberRequest("test2@example.com", "1234", "test2");
        memberB = Member.toEntity(request);

        memberRepository.save(memberA);
        memberRepository.save(memberB);
    }

    @Test
    void 팔로우() {
        // given
        Follow follow = Follow.builder()
                .follower(memberA)
                .following(memberB)
                .build();

        followRepository.save(follow);

        // when
        Follow foundFollow = followRepository.findById(follow.getId()).get();

        // then
        assertThat(foundFollow.getFollower()).isEqualTo(memberA);
        assertThat(foundFollow.getFollowing()).isEqualTo(memberB);
    }


}