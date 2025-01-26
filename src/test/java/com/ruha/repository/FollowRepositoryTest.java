package com.ruha.repository;

import com.ruha.dto.CreateFollowRequest;
import com.ruha.dto.CreateMemberRequest;
import com.ruha.entity.Follow;
import com.ruha.entity.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
class FollowRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private FollowRepository followRepository;

    @Test
    void 팔로우() {
        // given
        CreateMemberRequest request1 = new CreateMemberRequest("test1@example.com", "1234", "test");
        Member follower = Member.toEntity(request1);

        CreateMemberRequest request2 = new CreateMemberRequest("test2@example.com", "1234", "test");
        Member following = Member.toEntity(request2);

        memberRepository.saveAll(List.of(follower, following));

        // when
        Follow follow = Follow.toEntity(new CreateFollowRequest(follower, following));
        followRepository.save(follow);

        // then
        Assertions.assertThat(follow.getFollower()).isEqualTo(follower);
        Assertions.assertThat(follow.getFollowing()).isEqualTo(following);
    }

}