package com.ruha.service;

import com.ruha.dto.follow.FollowResponse;
import com.ruha.entity.Follow;
import com.ruha.entity.Member;
import com.ruha.exception.auth.UnauthorizedException;
import com.ruha.exception.follow.DuplicateFollowException;
import com.ruha.exception.follow.FollowNotFoundException;
import com.ruha.exception.follow.SelfFollowNotAllowedException;
import com.ruha.exception.member.MemberNotFoundException;
import com.ruha.repository.FollowRepository;
import com.ruha.repository.MemberRepository;
import com.ruha.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FollowService {

    private final FollowRepository followRepository;
    private final MemberRepository memberRepository;

    /**
     * 팔로우 하기
     *
     * @param targetMemberId 팔로우할 회원 ID
     * @return 생성된 팔로우 정보
     * @throws UnauthorizedException 인증되지 않은 경우 발생
     * @throws MemberNotFoundException 대상 회원을 찾을 수 없는 경우 발생
     * @throws SelfFollowNotAllowedException 자기 자신을 팔로우하려는 경우 발생
     * @throws DuplicateFollowException 이미 팔로우한 회원인 경우 발생
     */
    @Transactional
    public FollowResponse follow(Long targetMemberId) {
        Member currentMember = getCurrentAuthenticatedMember();
        Member targetMember = getMemberById(targetMemberId);
        
        // 자기 자신 팔로우 검증
        if (targetMember.equals(currentMember)) {
            throw new SelfFollowNotAllowedException();
        }
        
        // 중복 팔로우 검증 (DB 기반)
        if (followRepository.existsByFollowerAndFollowing(currentMember, targetMember)) {
            throw new DuplicateFollowException();
        }
        
        // Follow 엔티티 생성 및 저장
        Follow follow = Follow.builder()
                .follower(currentMember)
                .following(targetMember)
                .build();
        
        Follow savedFollow = followRepository.save(follow);
        
        return FollowResponse.from(savedFollow);
    }

    /**
     * 언팔로우 하기
     *
     * @param targetMemberId 언팔로우할 회원 ID
     * @throws UnauthorizedException 인증되지 않은 경우 발생
     * @throws MemberNotFoundException 대상 회원을 찾을 수 없는 경우 발생
     */
    @Transactional
    public void unfollow(Long targetMemberId) {
        Member currentMember = getCurrentAuthenticatedMember();
        Member targetMember = getMemberById(targetMemberId);
        
        // 팔로우 관계 조회 및 삭제
        Follow follow = followRepository.findByFollowerAndFollowing(currentMember, targetMember)
                .orElseThrow(FollowNotFoundException::new);
        
        followRepository.delete(follow);
    }

    /**
     * 팔로우 관계 확인
     *
     * @param targetMemberId 확인할 회원 ID
     * @return 팔로우 중이면 true, 아니면 false
     */
    public boolean isFollowing(Long targetMemberId) {
        Member currentMember = getCurrentAuthenticatedMember();
        Member targetMember = getMemberById(targetMemberId);
        
        return followRepository.existsByFollowerAndFollowing(currentMember, targetMember);
    }


    /**
     * 특정 회원의 팔로잉 목록 조회
     *
     * @param memberId 조회할 회원 ID
     * @return 해당 회원의 팔로잉 목록
     */
    public List<FollowResponse> getMemberFollowings(Long memberId) {
        Member member = getMemberById(memberId);
        List<Follow> followings = followRepository.findByFollower(member);
        
        return followings.stream()
                .map(FollowResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 특정 회원의 팔로워 목록 조회
     *
     * @param memberId 조회할 회원 ID
     * @return 해당 회원의 팔로워 목록
     */
    public List<FollowResponse> getMemberFollowers(Long memberId) {
        Member member = getMemberById(memberId);
        List<Follow> followers = followRepository.findByFollowing(member);
        
        return followers.stream()
                .map(FollowResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 현재 인증된 회원 정보를 조회합니다.
     *
     * @return 현재 로그인한 회원 엔티티
     * @throws UnauthorizedException 인증되지 않은 경우 발생
     * @throws MemberNotFoundException 회원을 찾을 수 없는 경우 발생
     */
    private Member getCurrentAuthenticatedMember() {
        Long memberId = SecurityUtil.getLoginMemberId()
                .orElseThrow(UnauthorizedException::new);
        
        return memberRepository.findByMemberIdAndIsDeletedFalse(memberId)
                .orElseThrow(MemberNotFoundException::new);
    }

    /**
     * 회원 ID로 회원 정보를 조회합니다.
     *
     * @param memberId 조회할 회원 ID
     * @return 회원 엔티티
     * @throws MemberNotFoundException 회원을 찾을 수 없는 경우 발생
     */
    private Member getMemberById(Long memberId) {
        return memberRepository.findByMemberIdAndIsDeletedFalse(memberId)
                .orElseThrow(MemberNotFoundException::new);
    }

}