package com.ruha.service;

import com.ruha.dto.member.request.*;
import com.ruha.dto.member.response.MemberDetailResponse;
import com.ruha.dto.member.response.MemberResponse;
import com.ruha.dto.member.response.TokenResponse;
import com.ruha.entity.Member;
import com.ruha.exception.auth.UnauthorizedException;
import com.ruha.exception.member.DuplicateNicknameException;
import com.ruha.exception.member.MemberNotFoundException;
import com.ruha.exception.member.PasswordMismatchException;
import com.ruha.jwt.JwtProvider;
import com.ruha.repository.CommentRepository;
import com.ruha.repository.FollowRepository;
import com.ruha.repository.MemberRepository;
import com.ruha.repository.TodoRepository;
import com.ruha.util.SecurityUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 회원 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final FollowRepository followRepository;
    private final CommentRepository commentRepository;
    private final TodoRepository todoRepository;

    /**
     * 새로운 회원을 생성합니다.
     *
     * @param request 회원 가입에 필요한 정보 (닉네임, 비밀번호, 이름)
     * @return 생성된 회원의 정보
     * @throws DuplicateNicknameException 닉네임이 중복될 경우 발생
     */
    @Transactional
    public MemberResponse createMember(CreateMemberRequest request) {

        if (memberRepository.existsByNickname(request.getNickname())) {
            throw new DuplicateNicknameException();
        }

        Member member = Member.builder()
                .nickname(request.getNickname())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .build();

        Member savedMember = memberRepository.save(member);

        return MemberResponse.from(savedMember, 0, 0, 0, 0, 0);

    }

    /**
     * 로그인을 처리하고 JWT 토큰을 발급합니다.
     *
     * @param request 로그인에 필요한 정보 (닉네임, 비밀번호)
     * @return 발급된 JWT 토큰
     * @throws MemberNotFoundException  해당 닉네임의 회원을 찾을 수 없을 경우 발생
     * @throws PasswordMismatchException 비밀번호가 일치하지 않을 경우 발생
     */
    public TokenResponse login(LoginRequest request) {
        Member member = memberRepository.findByNicknameAndIsDeletedFalse(request.getNickname())
                .orElseThrow(MemberNotFoundException::new);

        validatePassword(request.getPassword(), member.getPassword());

        String token = jwtProvider.createToken(member.getMemberId());
        return new TokenResponse(token);
    }

    /**
     * 현재 로그인한 회원의 정보를 조회합니다.
     *
     * @return 현재 회원의 정보
     * @throws UnauthorizedException 인증되지 않은 경우 발생
     * @throws MemberNotFoundException 회원을 찾을 수 없을 경우 발생
     */
    public MemberResponse getCurrentMemberInfo() {
        Member member = getCurrentAuthenticatedMember();
        MemberStatistics stats = getMemberStatistics(member);

        return MemberResponse.from(member,
            stats.followerCount, stats.followingCount,
            stats.commentCount, stats.totalTodoCount, stats.completedTodoCount);
    }

    /**
     * 현재 로그인한 회원의 이름을 수정합니다.
     *
     * @param request 변경할 새로운 이름
     * @throws UnauthorizedException 인증되지 않은 경우 발생
     * @throws MemberNotFoundException 회원을 찾을 수 없을 경우 발생
     */
    @Transactional
    public MemberResponse updateName(UpdateMemberRequest request) {
        Member member = getCurrentAuthenticatedMember();
        member.updateName(request.getName());

        return getCurrentMemberInfo();
    }

    /**
     * 현재 로그인한 회원의 비밀번호를 변경합니다.
     *
     * @param request 현재 비밀번호와 새로운 비밀번호
     * @throws UnauthorizedException 인증되지 않은 경우 발생
     * @throws MemberNotFoundException 회원을 찾을 수 없을 경우 발생
     * @throws PasswordMismatchException 현재 비밀번호가 일치하지 않을 경우 발생
     */
    @Transactional
    public MemberResponse updatePassword(PasswordChangeRequest request) {
        Member member = getCurrentAuthenticatedMember();
        validatePassword(request.getCurrentPassword(), member.getPassword());
        member.updatePassword(passwordEncoder.encode(request.getNewPassword()));

        return getCurrentMemberInfo();
    }

    /**
     * 현재 로그인한 회원을 탈퇴 처리합니다. (소프트 삭제)
     *
     * @param request 탈퇴 확인을 위한 비밀번호
     * @throws UnauthorizedException 인증되지 않은 경우 발생
     * @throws MemberNotFoundException 회원을 찾을 수 없을 경우 발생
     * @throws PasswordMismatchException 비밀번호가 일치하지 않을 경우 발생
     */
    @Transactional
    public void deleteMember(DeleteMemberRequest request) {
        Member member = getCurrentAuthenticatedMember();
        validatePassword(request.getPassword(), member.getPassword());
        member.delete();
    }

    /**
     * 특정 회원의 정보를 조회합니다.
     * 로그인한 경우 팔로우 여부를 포함하고, 비로그인한 경우 팔로우 여부는 null 입니다.
     *
     * @param memberId 조회할 회원의 ID
     * @return 회원 상세 정보
     * @throws MemberNotFoundException 회원을 찾을 수 없는 경우 발생
     */
    public MemberDetailResponse getMemberInfo(Long memberId) {

        Member targetMember = memberRepository.findByMemberIdAndIsDeletedFalse(memberId)
                .orElseThrow(MemberNotFoundException::new);

        MemberStatistics stats = getMemberStatistics(targetMember);

        // 로그인 확인
        Optional<Long> currentMemberId = SecurityUtil.getLoginMemberId();

        // 비로그인 시
        if (currentMemberId.isEmpty()) {
            return MemberDetailResponse.from(targetMember,
                stats.followerCount, stats.followingCount,
                stats.commentCount, stats.totalTodoCount, stats.completedTodoCount);
        }

        // 로그인한 사용자
        Member currentMember = memberRepository.findByMemberIdAndIsDeletedFalse(currentMemberId.get())
                .orElseThrow(MemberNotFoundException::new);

        boolean isFollowing = followRepository.existsByFollowerAndFollowing(currentMember, targetMember);

        return MemberDetailResponse.from(targetMember,
            stats.followerCount, stats.followingCount,
            stats.commentCount, stats.totalTodoCount, stats.completedTodoCount, isFollowing);

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
     * 비밀번호 일치 여부를 검증합니다.
     *
     * @param rawPassword 평문 비밀번호
     * @param encodedPassword 암호화된 비밀번호
     * @throws PasswordMismatchException 비밀번호가 일치하지 않는 경우 발생
     */
    private void validatePassword(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new PasswordMismatchException();
        }
    }

    /**
     * 회원의 통계 정보를 조회합니다.
     *
     * @param member 통계를 조회할 회원
     * @return 회원의 통계 정보 (팔로워, 팔로잉, 댓글, 할일 수)
     */
    private MemberStatistics getMemberStatistics(Member member) {
        return new MemberStatistics(
            followRepository.countByFollowing(member),
            followRepository.countByFollower(member),
            commentRepository.countByMember(member),
            todoRepository.countByMember(member),
            todoRepository.countByMemberAndIsCompleted(member, true)
        );
    }

    /**
     * 회원 통계 정보를 담는 내부 클래스입니다.
     */
    @Getter
    @AllArgsConstructor
    private static class MemberStatistics {
        private final long followerCount;
        private final long followingCount;
        private final long commentCount;
        private final long totalTodoCount;
        private final long completedTodoCount;
    }

}
