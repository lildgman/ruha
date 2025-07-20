package com.ruha.service;

import com.ruha.dto.member.CreateMemberRequest;
import com.ruha.dto.member.MemberResponse;
import com.ruha.entity.Member;
import com.ruha.exception.member.DuplicateNicknameException;
import com.ruha.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    // 테스트하고 싶은 클래스
    // @Mock으로 만든 가짜 객체들이 자동으로 주입
    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("회원가입 - 성공")
    void 회원가입() {

        // given
        CreateMemberRequest request = new CreateMemberRequest("user", "password1234", "테스터");

        when(memberRepository.existsByNickname("user")).thenReturn(false);
        when(passwordEncoder.encode("password1234")).thenReturn("encoded_password");

        Member savedMember = Member.builder()
                .memberId(1L)
                .nickname("user")
                .password("encoded_password")
                .name("테스터")
                .build();
        when(memberRepository.save(any(Member.class))).thenReturn(savedMember);

        // when
        MemberResponse response = memberService.createMember(request);

        // then
        assertThat(response.getMemberId()).isEqualTo(1L);
        assertThat(response.getNickname()).isEqualTo("user");
        assertThat(response.getName()).isEqualTo("테스터");

        verify(memberRepository, times(1)).existsByNickname("user");
        verify(passwordEncoder, times(1)).encode("password1234");
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    @DisplayName("닉네임 중복 시 예외 발생")
    void 회원가입_실패_닉네임_중복() {

        // given
        CreateMemberRequest request = new CreateMemberRequest("duplicateMember", "password1234", "중복맨");

        when(memberRepository.existsByNickname("duplicateMember")).thenReturn(true);

        // when & then
        assertThrows(DuplicateNicknameException.class,
                () -> memberService.createMember(request));

        verify(passwordEncoder, never()).encode(anyString());
        verify(memberRepository, never()).save(any(Member.class));
    }

}