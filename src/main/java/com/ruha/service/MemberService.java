package com.ruha.service;

import com.ruha.dto.member.CreateMemberRequest;
import com.ruha.dto.member.LoginRequest;
import com.ruha.dto.member.MemberResponse;
import com.ruha.dto.member.TokenResponse;
import com.ruha.entity.Member;
import com.ruha.exception.member.DuplicateNicknameException;
import com.ruha.exception.member.MemberErrorCode;
import com.ruha.exception.member.MemberNotFoundException;
import com.ruha.exception.member.PasswordMismatchException;
import com.ruha.jwt.JwtProvider;
import com.ruha.repository.MemberRepository;
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

    @Transactional
    public MemberResponse createMember(CreateMemberRequest request) {

        if (memberRepository.existsByNickname(request.getNickname())) {
            throw new DuplicateNicknameException("이미 사용 중인 닉네임입니다.");
        }

        Member member = Member.builder()
                .nickname(request.getNickname())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .build();

        Member savedMember = memberRepository.save(member);

        return MemberResponse.from(savedMember);

    }

    public TokenResponse login(LoginRequest request) {
        Member member = memberRepository.findByNickname(request.getNickname())
                .orElseThrow(() -> new MemberNotFoundException(MemberErrorCode.MEMBER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new PasswordMismatchException(MemberErrorCode.PASSWORD_MISMATCH);
        }

        String token = jwtProvider.createToken(member.getMemberId());
        return new TokenResponse(token);
    }

}
