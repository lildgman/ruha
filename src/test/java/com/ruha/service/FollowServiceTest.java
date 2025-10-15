package com.ruha.service;

import com.ruha.dto.follow.response.FollowResponse;
import com.ruha.entity.Follow;
import com.ruha.entity.Member;
import com.ruha.exception.follow.DuplicateFollowException;
import com.ruha.exception.follow.SelfFollowNotAllowedException;
import com.ruha.exception.member.MemberNotFoundException;
import com.ruha.repository.FollowRepository;
import com.ruha.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
class FollowServiceTest {

    @Autowired
    private FollowService followService;

    @MockBean
    private FollowRepository followRepository;

    @MockBean
    private MemberRepository memberRepository;

    @Nested
    @DisplayName("팔로우")
    class FollowMember {

        @Test
        @WithMockUser(username = "1")
        @DisplayName("성공")
        void 팔로우_성공() {

            // given
            Long currentMemberId = 1L;
            Long targetMemberId = 2L;

            Member currentMember = Member.builder()
                    .memberId(currentMemberId)
                    .nickname("follower")
                    .name("팔로워")
                    .build();

            Member targetMember = Member.builder()
                    .memberId(targetMemberId)
                    .nickname("following")
                    .name("팔로잉")
                    .build();

            Follow savedFollow = Follow.builder()
                    .followId(1L)
                    .follower(currentMember)
                    .following(targetMember)
                    .build();

            when(memberRepository.findByMemberIdAndIsDeletedFalse(currentMemberId))
                    .thenReturn(Optional.of(currentMember));
            when(memberRepository.findByMemberIdAndIsDeletedFalse(targetMemberId))
                    .thenReturn(Optional.of(targetMember));
            when(followRepository.existsByFollowerAndFollowing(currentMember, targetMember))
                    .thenReturn(false);
            when(followRepository.save(any(Follow.class)))
                    .thenReturn(savedFollow);

            // when
            FollowResponse result = followService.follow(targetMemberId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getFollowId()).isEqualTo(1L);
            assertThat(result.getFollowerNickname()).isEqualTo("follower");
            assertThat(result.getFollowingNickname()).isEqualTo("following");

            verify(memberRepository, times(1)).findByMemberIdAndIsDeletedFalse(currentMemberId);
            verify(memberRepository, times(1)).findByMemberIdAndIsDeletedFalse(targetMemberId);
            verify(followRepository, times(1)).existsByFollowerAndFollowing(currentMember, targetMember);
            verify(followRepository, times(1)).save(any(Follow.class));
        }

        @Test
        @WithMockUser(username = "1")
        @DisplayName("실패 - 자기 자신 팔로우")
        void 자기_자신_팔로우_실패() {
            // given
            Long memberId = 1L;
            
            Member member = Member.builder()
                    .memberId(memberId)
                    .nickname("user")
                    .name("사용자")
                    .build();

            when(memberRepository.findByMemberIdAndIsDeletedFalse(memberId))
                    .thenReturn(Optional.of(member));

            // when & then
            assertThrows(SelfFollowNotAllowedException.class, 
                    () -> followService.follow(memberId));
        }

        @Test
        @WithMockUser(username = "1")
        @DisplayName("실패 - 이미 팔로우한 회원")
        void 중복_팔로우_실패() {
            // given
            Long currentMemberId = 1L;
            Long targetMemberId = 2L;
            
            Member currentMember = Member.builder()
                    .memberId(currentMemberId)
                    .nickname("follower")
                    .name("팔로워")
                    .build();
            
            Member targetMember = Member.builder()
                    .memberId(targetMemberId)
                    .nickname("following")
                    .name("팔로잉")
                    .build();

            when(memberRepository.findByMemberIdAndIsDeletedFalse(currentMemberId))
                    .thenReturn(Optional.of(currentMember));
            when(memberRepository.findByMemberIdAndIsDeletedFalse(targetMemberId))
                    .thenReturn(Optional.of(targetMember));
            when(followRepository.existsByFollowerAndFollowing(currentMember, targetMember))
                    .thenReturn(true);

            // when & then
            assertThrows(DuplicateFollowException.class, 
                    () -> followService.follow(targetMemberId));
        }

        @Test
        @WithMockUser(username = "1")
        @DisplayName("실패 - 존재하지 않는 대상 회원")
        void 존재하지_않는_대상_회원_팔로우_실패() {
            // given
            Long currentMemberId = 1L;
            Long targetMemberId = 999L;
            
            Member currentMember = Member.builder()
                    .memberId(currentMemberId)
                    .nickname("follower")
                    .name("팔로워")
                    .build();

            when(memberRepository.findByMemberIdAndIsDeletedFalse(currentMemberId))
                    .thenReturn(Optional.of(currentMember));
            when(memberRepository.findByMemberIdAndIsDeletedFalse(targetMemberId))
                    .thenReturn(Optional.empty());

            // when & then
            assertThrows(MemberNotFoundException.class, 
                    () -> followService.follow(targetMemberId));
        }
    }

    @Nested
    @DisplayName("언팔로우")
    class UnfollowMember {

        @Test
        @WithMockUser(username = "1")
        @DisplayName("성공")
        void 언팔로우_성공() {
            // given
            Long currentMemberId = 1L;
            Long targetMemberId = 2L;
            
            Member currentMember = Member.builder()
                    .memberId(currentMemberId)
                    .nickname("follower")
                    .name("팔로워")
                    .build();
            
            Member targetMember = Member.builder()
                    .memberId(targetMemberId)
                    .nickname("following")
                    .name("팔로잉")
                    .build();

            Follow follow = Follow.builder()
                    .follower(currentMember)
                    .following(targetMember)
                    .build();

            when(memberRepository.findByMemberIdAndIsDeletedFalse(currentMemberId))
                    .thenReturn(Optional.of(currentMember));
            when(memberRepository.findByMemberIdAndIsDeletedFalse(targetMemberId))
                    .thenReturn(Optional.of(targetMember));
            when(followRepository.findByFollowerAndFollowing(currentMember, targetMember))
                    .thenReturn(Optional.of(follow));

            // when
            followService.unfollow(targetMemberId);

            // then
            verify(memberRepository, times(1)).findByMemberIdAndIsDeletedFalse(currentMemberId);
            verify(memberRepository, times(1)).findByMemberIdAndIsDeletedFalse(targetMemberId);
            verify(followRepository, times(1)).findByFollowerAndFollowing(currentMember, targetMember);
            verify(followRepository, times(1)).delete(follow);
        }

        @Test
        @WithMockUser(username = "1")
        @DisplayName("실패 - 존재하지 않는 대상 회원")
        void 존재하지_않는_대상_회원_언팔로우_실패() {
            // given
            Long currentMemberId = 1L;
            Long targetMemberId = 999L;
            
            Member currentMember = Member.builder()
                    .memberId(currentMemberId)
                    .nickname("follower")
                    .name("팔로워")
                    .build();

            when(memberRepository.findByMemberIdAndIsDeletedFalse(currentMemberId))
                    .thenReturn(Optional.of(currentMember));
            when(memberRepository.findByMemberIdAndIsDeletedFalse(targetMemberId))
                    .thenReturn(Optional.empty());

            // when & then
            assertThrows(MemberNotFoundException.class, 
                    () -> followService.unfollow(targetMemberId));
        }
    }

    @Nested
    @DisplayName("팔로우 관계 확인")
    class IsFollowing {

        @Test
        @WithMockUser(username = "1")
        @DisplayName("팔로우 중인 경우")
        void 팔로우_중인_경우() {
            // given
            Long currentMemberId = 1L;
            Long targetMemberId = 2L;
            
            Member currentMember = Member.builder()
                    .memberId(currentMemberId)
                    .nickname("follower")
                    .name("팔로워")
                    .build();
            
            Member targetMember = Member.builder()
                    .memberId(targetMemberId)
                    .nickname("following")
                    .name("팔로잉")
                    .build();

            when(memberRepository.findByMemberIdAndIsDeletedFalse(currentMemberId))
                    .thenReturn(Optional.of(currentMember));
            when(memberRepository.findByMemberIdAndIsDeletedFalse(targetMemberId))
                    .thenReturn(Optional.of(targetMember));
            when(followRepository.existsByFollowerAndFollowing(currentMember, targetMember))
                    .thenReturn(true);

            // when
            boolean result = followService.isFollowing(targetMemberId);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @WithMockUser(username = "1")
        @DisplayName("팔로우하지 않은 경우")
        void 팔로우하지_않은_경우() {
            // given
            Long currentMemberId = 1L;
            Long targetMemberId = 2L;
            
            Member currentMember = Member.builder()
                    .memberId(currentMemberId)
                    .nickname("follower")
                    .name("팔로워")
                    .build();
            
            Member targetMember = Member.builder()
                    .memberId(targetMemberId)
                    .nickname("following")
                    .name("팔로잉")
                    .build();

            when(memberRepository.findByMemberIdAndIsDeletedFalse(currentMemberId))
                    .thenReturn(Optional.of(currentMember));
            when(memberRepository.findByMemberIdAndIsDeletedFalse(targetMemberId))
                    .thenReturn(Optional.of(targetMember));
            when(followRepository.existsByFollowerAndFollowing(currentMember, targetMember))
                    .thenReturn(false);

            // when
            boolean result = followService.isFollowing(targetMemberId);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("특정 회원의 팔로잉 목록 조회")
    class GetMemberFollowings {

        @Test
        @DisplayName("성공")
        void 특정_회원의_팔로잉_목록_조회_성공() {
            // given
            Long memberId = 1L;
            
            Member member = Member.builder()
                    .memberId(memberId)
                    .nickname("user")
                    .name("사용자")
                    .build();
            
            Member following1 = Member.builder()
                    .memberId(2L)
                    .nickname("following1")
                    .name("팔로잉1")
                    .build();

            Follow follow1 = Follow.builder()
                    .follower(member)
                    .following(following1)
                    .build();

            when(memberRepository.findByMemberIdAndIsDeletedFalse(memberId))
                    .thenReturn(Optional.of(member));
            when(followRepository.findByFollower(member))
                    .thenReturn(List.of(follow1));

            // when
            List<FollowResponse> result = followService.getMemberFollowings(memberId);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getFollowingNickname()).isEqualTo("following1");
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 회원")
        void 존재하지_않는_회원의_팔로잉_목록_조회_실패() {
            // given
            Long memberId = 999L;

            when(memberRepository.findByMemberIdAndIsDeletedFalse(memberId))
                    .thenReturn(Optional.empty());

            // when & then
            assertThrows(MemberNotFoundException.class, 
                    () -> followService.getMemberFollowings(memberId));
        }
    }

    @Nested
    @DisplayName("특정 회원의 팔로워 목록 조회")
    class GetMemberFollowers {

        @Test
        @DisplayName("성공")
        void 특정_회원의_팔로워_목록_조회_성공() {
            // given
            Long memberId = 1L;
            
            Member member = Member.builder()
                    .memberId(memberId)
                    .nickname("user")
                    .name("사용자")
                    .build();
            
            Member follower1 = Member.builder()
                    .memberId(2L)
                    .nickname("follower1")
                    .name("팔로워1")
                    .build();

            Follow follow1 = Follow.builder()
                    .follower(follower1)
                    .following(member)
                    .build();

            when(memberRepository.findByMemberIdAndIsDeletedFalse(memberId))
                    .thenReturn(Optional.of(member));
            when(followRepository.findByFollowing(member))
                    .thenReturn(List.of(follow1));

            // when
            List<FollowResponse> result = followService.getMemberFollowers(memberId);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getFollowerNickname()).isEqualTo("follower1");
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 회원")
        void 존재하지_않는_회원의_팔로워_목록_조회_실패() {
            // given
            Long memberId = 999L;

            when(memberRepository.findByMemberIdAndIsDeletedFalse(memberId))
                    .thenReturn(Optional.empty());

            // when & then
            assertThrows(MemberNotFoundException.class, 
                    () -> followService.getMemberFollowers(memberId));
        }
    }

}