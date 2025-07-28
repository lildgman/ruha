package com.ruha.service;

import com.ruha.dto.member.CreateMemberRequest;
import com.ruha.dto.member.LoginRequest;
import com.ruha.dto.member.MemberResponse;
import com.ruha.dto.member.TokenResponse;
import com.ruha.entity.Member;
import com.ruha.exception.member.DuplicateNicknameException;
import com.ruha.exception.member.MemberNotFoundException;
import com.ruha.exception.member.PasswordMismatchException;
import com.ruha.jwt.JwtProvider;
import com.ruha.repository.CommentRepository;
import com.ruha.repository.FollowRepository;
import com.ruha.repository.MemberRepository;
import com.ruha.repository.TodoRepository;
import com.ruha.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


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

        return MemberResponse.of(savedMember, 0, 0, 0, 0, 0);

    }

    public TokenResponse login(LoginRequest request) {
        Member member = memberRepository.findByNickname(request.getNickname())
                .orElseThrow(MemberNotFoundException::new);

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new PasswordMismatchException();
        }

        String token = jwtProvider.createToken(member.getMemberId());
        return new TokenResponse(token);
    }

    public MemberResponse getCurrentMemberInfo() {
        Long memberId = SecurityUtil.getLoginMemberId()
                .orElseThrow(MemberNotFoundException::new);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        long followerCount = followRepository.countByFollowing(member);
        long followingCount = followRepository.countByFollower(member);
        long commentCount = commentRepository.countByMember(member);
        long totalTodoCount = todoRepository.countByMember(member);
        long completedTodoCount = todoRepository.countByMemberAndIsCompleted(member, true);

        return MemberResponse.of(member, followerCount, followingCount, commentCount, totalTodoCount, completedTodoCount);
    }



}
