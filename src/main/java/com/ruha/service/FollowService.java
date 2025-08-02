package com.ruha.service;

import com.ruha.dto.follow.FollowCountResponse;
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
     * @throws UnauthorizedException 인증되지 않은 경우 발생
     * @throws MemberNotFoundException 대상 회원을 찾을 수 없는 경우 발생
     * @throws SelfFollowNotAllowedException 자기 자신을 팔로우하려는 경우 발생
     * @throws DuplicateFollowException 이미 팔로우한 회원인 경우 발생
     */
    @Transactional
    public void follow(Long targetMemberId) {
        Member currentMember = getCurrentAuthenticatedMember();
        Member targetMember = getMemberById(targetMemberId);
        
        // Member 엔티티의 비즈니스 메서드 활용
        currentMember.follow(targetMember);
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
        
        // Member 엔티티의 비즈니스 메서드 활용
        currentMember.unfollow(targetMember);
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
     * 내 팔로잉 목록 조회
     *
     * @return 팔로잉 중인 회원들의 목록
     */
    public List<FollowResponse> getFollowings() {
        Member currentMember = getCurrentAuthenticatedMember();
        List<Follow> followings = followRepository.findByFollower(currentMember);
        
        return followings.stream()
                .map(FollowResponse::of)
                .collect(Collectors.toList());
    }

    /**
     * 내 팔로워 목록 조회
     *
     * @return 나를 팔로우하는 회원들의 목록
     */
    public List<FollowResponse> getFollowers() {
        Member currentMember = getCurrentAuthenticatedMember();
        List<Follow> followers = followRepository.findByFollowing(currentMember);
        
        return followers.stream()
                .map(FollowResponse::of)
                .collect(Collectors.toList());
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
                .map(FollowResponse::of)
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
                .map(FollowResponse::of)
                .collect(Collectors.toList());
    }

    /**
     * 팔로잉/팔로워 수 조회
     *
     * @param memberId 조회할 회원 ID
     * @return 팔로잉 수와 팔로워 수
     */
    public FollowCountResponse getFollowCount(Long memberId) {
        Member member = getMemberById(memberId);
        
        long followingCount = followRepository.countByFollower(member);
        long followerCount = followRepository.countByFollowing(member);
        
        return new FollowCountResponse(followingCount, followerCount);
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