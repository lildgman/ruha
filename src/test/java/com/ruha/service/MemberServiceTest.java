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
import com.ruha.repository.MemberRepository;
import com.ruha.util.SecurityUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

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

    @Mock
    private JwtProvider jwtProvider;

    @Nested
    @DisplayName("회원가입")
    class SignUp {
        @Test
        @DisplayName("성공")
        void success() {

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
        @DisplayName("실패 - 닉네임 중복")
        void fail_duplicate_nickname() {

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


    @Nested
    @DisplayName("로그인")
    class Login {
        @Test
        @DisplayName("성공")
        void success() {
            // given
            LoginRequest request = new LoginRequest("user", "password1234");
            Member member = Member.builder()
                    .memberId(1L)
                    .nickname("user")
                    .password("encoded_password")
                    .name("테스터")
                    .build();

            when(memberRepository.findByNickname("user")).thenReturn(Optional.of(member));
            when(passwordEncoder.matches("password1234", "encoded_password")).thenReturn(true);
            when(jwtProvider.createToken(1L)).thenReturn("test_token");

            // when
            TokenResponse response = memberService.login(request);

            // then
            assertThat(response.getAccessToken()).isEqualTo("test_token");

            verify(memberRepository, times(1)).findByNickname("user");
            verify(passwordEncoder, times(1)).matches("password1234", "encoded_password");
            verify(jwtProvider, times(1)).createToken(1L);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 회원")
        void fail_member_not_found() {
            // given
            LoginRequest request = new LoginRequest("non_exist_user", "password1234");

            when(memberRepository.findByNickname("non_exist_user")).thenReturn(Optional.empty());

            // when & then
            assertThrows(MemberNotFoundException.class,
                    () -> memberService.login(request));

            verify(passwordEncoder, never()).matches(anyString(), anyString());
            verify(jwtProvider, never()).createToken(anyLong());
        }

        @Test
        @DisplayName("실패 - 비밀번호 불일치")
        void fail_password_mismatch() {
            // given
            LoginRequest request = new LoginRequest("user", "wrong_password");
            Member member = Member.builder()
                    .memberId(1L)
                    .nickname("user")
                    .password("encoded_password")
                    .name("테스터")
                    .build();

            when(memberRepository.findByNickname("user")).thenReturn(Optional.of(member));
            when(passwordEncoder.matches("wrong_password", "encoded_password")).thenReturn(false);

            // when & then
            assertThrows(PasswordMismatchException.class,
                    () -> memberService.login(request));

            verify(jwtProvider, never()).createToken(anyLong());
        }
    }

    @Nested
    @DisplayName("내 정보 조회")
    class GetMyInfo {

        @Test
        @DisplayName("성공")
        void success() {
            // given
            Long currentMemberId = 1L;
            Member member = Member.builder()
                    .memberId(currentMemberId)
                    .nickname("user")
                    .name("테스터")
                    .build();

            try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
                securityUtil.when(SecurityUtil::getLoginMemberId).thenReturn(Optional.of(currentMemberId));
                when(memberRepository.findById(currentMemberId)).thenReturn(Optional.of(member));

                // when
                MemberResponse response = memberService.getCurrentMemberInfo();

                // then
                assertThat(response.getMemberId()).isEqualTo(currentMemberId);
                assertThat(response.getNickname()).isEqualTo("user");
                assertThat(response.getName()).isEqualTo("테스터");

                verify(memberRepository, times(1)).findById(currentMemberId);
            }
        }

        @Test
        @DisplayName("실패 - 인증되지 않은 사용자")
        void fail_unauthenticated() {
            // given
            try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
                securityUtil.when(SecurityUtil::getLoginMemberId).thenReturn(Optional.empty());

                // when & then
                assertThrows(MemberNotFoundException.class,
                        () -> memberService.getCurrentMemberInfo());

                verify(memberRepository, never()).findById(anyLong());
            }
        }

        @Test
        @DisplayName("실패 - DB에 해당 유저 없음")
        void fail_member_not_found_in_db() {
            // given
            Long currentMemberId = 1L;

            try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
                securityUtil.when(SecurityUtil::getLoginMemberId).thenReturn(Optional.of(currentMemberId));
                when(memberRepository.findById(currentMemberId)).thenReturn(Optional.empty());

                // when & then
                assertThrows(MemberNotFoundException.class,
                        () -> memberService.getCurrentMemberInfo());

                verify(memberRepository, times(1)).findById(currentMemberId);
            }
        }
    }
}