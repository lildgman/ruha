package com.ruha.service;

import com.ruha.dto.member.request.*;
import com.ruha.dto.member.response.MemberResponse;
import com.ruha.dto.member.response.TokenResponse;
import com.ruha.entity.Member;
import com.ruha.exception.member.DuplicateNicknameException;
import com.ruha.exception.member.MemberNotFoundException;
import com.ruha.exception.member.PasswordMismatchException;
import com.ruha.jwt.JwtProvider;
import com.ruha.repository.CommentRepository;
import com.ruha.repository.FollowRepository;
import com.ruha.repository.MemberRepository;
import com.ruha.repository.TodoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @MockBean
    private MemberRepository memberRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtProvider jwtProvider;

    @MockBean
    private FollowRepository followRepository;

    @MockBean
    private CommentRepository commentRepository;

    @MockBean
    private TodoRepository todoRepository;

    @Nested
    @DisplayName("회원가입")
    class SignUp {
        @Test
        @DisplayName("성공")
        void success() {
            // given
            CreateMemberRequest request = new CreateMemberRequest("user", "password1234", "테스터");
            Member savedMember = Member.builder().memberId(1L).nickname("user").password("encoded_password").name("테스터").build();

            when(memberRepository.existsByNickname("user")).thenReturn(false);
            when(passwordEncoder.encode("password1234")).thenReturn("encoded_password");
            when(memberRepository.save(any(Member.class))).thenReturn(savedMember);

            // when
            MemberResponse response = memberService.createMember(request);

            // then
            assertThat(response.getMemberId()).isEqualTo(1L);
            assertThat(response.getNickname()).isEqualTo("user");
            assertThat(response.getName()).isEqualTo("테스터");
        }

        @Test
        @DisplayName("실패 - 닉네임 중복")
        void fail_duplicate_nickname() {
            // given
            CreateMemberRequest request = new CreateMemberRequest("duplicateMember", "password1234", "중복맨");
            when(memberRepository.existsByNickname("duplicateMember")).thenReturn(true);

            // when & then
            assertThrows(DuplicateNicknameException.class, () -> memberService.createMember(request));
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
            Member member = Member.builder().memberId(1L).nickname("user").password("encoded_password").name("테스터").build();

            when(memberRepository.findByNicknameAndIsDeletedFalse("user")).thenReturn(Optional.of(member));
            when(passwordEncoder.matches("password1234", "encoded_password")).thenReturn(true);
            when(jwtProvider.createToken(1L)).thenReturn("test_token");

            // when
            TokenResponse response = memberService.login(request);

            // then
            assertThat(response.getAccessToken()).isEqualTo("test_token");
        }

        @Test
        @DisplayName("실패 - 가입되지 않은 닉네임")
        void fail_member_not_found() {

            LoginRequest request = new LoginRequest("non_existent_user", "password1234");
            when(memberRepository.findByNicknameAndIsDeletedFalse("non_existent_user")).thenReturn(Optional.empty());

            assertThrows(MemberNotFoundException.class, () -> memberService.login(request));
        }

        @Test
        @DisplayName("실패 - 비밀번호 불일치")
        void fail_password_mismatch() {

            LoginRequest request = new LoginRequest("user", "wrong_password");

            Member member = Member.builder()
                    .memberId(1L)
                    .nickname("user")
                    .password("encoded_password")
                    .name("test")
                    .build();

            when(memberRepository.findByNicknameAndIsDeletedFalse("user")).thenReturn(Optional.of(member));
            when(passwordEncoder.matches("wrong_password", "encoded_password")).thenReturn(false);

            assertThrows(PasswordMismatchException.class, () -> memberService.login(request));
        }
    }

    @Nested
    @DisplayName("내 정보 조회")
    class GetMyInfo {

        @Test
        @WithMockUser(username = "1") // SecurityContext에 사용자 ID 1을 설정
        @DisplayName("성공")
        void success() {
            // given
            Long currentMemberId = 1L;
            Member member = Member.builder().memberId(currentMemberId).nickname("user").name("테스터").build();

            when(memberRepository.findByMemberIdAndIsDeletedFalse(currentMemberId)).thenReturn(Optional.of(member));
            when(followRepository.countByFollowing(member)).thenReturn(5L);
            when(followRepository.countByFollower(member)).thenReturn(10L);
            when(commentRepository.countByMember(member)).thenReturn(3L);
            when(todoRepository.countByMember(member)).thenReturn(20L);
            when(todoRepository.countByMemberAndIsCompleted(member, true)).thenReturn(10L);

            // when
            MemberResponse response = memberService.getCurrentMemberInfo();

            // then
            assertThat(response.getMemberId()).isEqualTo(currentMemberId);
            assertThat(response.getTodoCompletionRate()).isEqualTo(0.5);
        }

        @Test
        @WithMockUser(username = "1")
        @DisplayName("실패 - DB에 존재하지 않는 회원")
        void fail_member_not_found() {

            Long currentMemberId = 1L;
            when(memberRepository.findByMemberIdAndIsDeletedFalse(currentMemberId)).thenReturn(Optional.empty());

            assertThrows(MemberNotFoundException.class, () -> memberService.getCurrentMemberInfo());
        }
    }

    @Nested
    @DisplayName("이름 수정")
    class UpdateName {

        @Test
        @WithMockUser(username = "1") // SecurityContext에 사용자 ID 1을 설정
        @DisplayName("성공")
        void success() {
            // given
            Long currentMemberId = 1L;
            Member member = Member.builder().memberId(currentMemberId).nickname("user").name("기존이름").build();
            UpdateMemberRequest request = new UpdateMemberRequest("새이름");

            when(memberRepository.findByMemberIdAndIsDeletedFalse(currentMemberId)).thenReturn(Optional.of(member));
            when(followRepository.countByFollowing(member)).thenReturn(0L);
            when(followRepository.countByFollower(member)).thenReturn(0L);
            when(commentRepository.countByMember(member)).thenReturn(0L);
            when(todoRepository.countByMember(member)).thenReturn(0L);
            when(todoRepository.countByMemberAndIsCompleted(member, true)).thenReturn(0L);

            // when
            memberService.updateName(request);

            // then
            assertThat(member.getName()).isEqualTo("새이름");
            verify(memberRepository, times(2)).findByMemberIdAndIsDeletedFalse(currentMemberId); // getCurrentAuthenticatedMember + getCurrentMemberInfo
        }

        @Test
        @DisplayName("실패 - DB에 해당 유저 없음")
        @WithMockUser(username = "1")
        void fail_member_not_found_in_db() {
            // given
            Long currentMemberId = 1L;
            UpdateMemberRequest request = new UpdateMemberRequest("새이름");

            when(memberRepository.findByMemberIdAndIsDeletedFalse(currentMemberId)).thenReturn(Optional.empty());

            // when & then
            assertThrows(MemberNotFoundException.class, () -> memberService.updateName(request));
        }
    }

    @Nested
    @DisplayName("비밀번호 수정")
    class UpdatePassword {

        @Test
        @WithMockUser(username = "1")
        @DisplayName("성공")
        void success() {

            // given
            Long currentMemberId = 1L;
            Member member = Member.builder()
                    .memberId(currentMemberId)
                    .password("encoded_current_password")
                    .build();

            PasswordChangeRequest request = new PasswordChangeRequest("current_password", "new_password");

            when(memberRepository.findByMemberIdAndIsDeletedFalse(currentMemberId)).thenReturn(Optional.of(member));
            when(passwordEncoder.matches("current_password", "encoded_current_password")).thenReturn(true);
            when(passwordEncoder.encode("new_password")).thenReturn("encoded_new_password");

            // when
            memberService.updatePassword(request);

            // then
            assertThat(member.getPassword()).isEqualTo("encoded_new_password");
            verify(passwordEncoder, times(1)).encode("new_password");

        }

        @Test
        @WithMockUser(username = "1")
        @DisplayName("실패 - 현재 비밀번호 불일치")
        void fail_password_mismatch() {

            // given
            Long currentMemberId = 1L;
            Member member = Member.builder()
                    .memberId(currentMemberId)
                    .password("encoded_current_password")
                    .build();
            PasswordChangeRequest request = new PasswordChangeRequest("wrong_password", "new_password");

            when(memberRepository.findByMemberIdAndIsDeletedFalse(currentMemberId)).thenReturn(Optional.of(member));
            when(passwordEncoder.matches("wrong_password", "encoded_current_password")).thenReturn(false);

            assertThrows(PasswordMismatchException.class, () -> memberService.updatePassword(request));
            verify(passwordEncoder, never()).encode(anyString());
        }

        @Test
        @WithMockUser(username = "1")
        @DisplayName("실패 - 존재하지 않은 사용자")
        void fail_member_not_found() {

            Long currentMemberId = 1L;
            PasswordChangeRequest request = new PasswordChangeRequest("any_password", "new_password");

            when(memberRepository.findByMemberIdAndIsDeletedFalse(currentMemberId)).thenReturn(Optional.empty());

            assertThrows(MemberNotFoundException.class, () -> memberService.updatePassword(request));

        }
    }

    @Nested
    @DisplayName("회원 탈퇴")
    class DeleteMember {

        @Test
        @WithMockUser(username = "1")
        @DisplayName("성공")
        void success() {

            // given
            Long currentMemberId = 1L;
            Member member = Member.builder()
                    .memberId(currentMemberId)
                    .nickname("user")
                    .password("encoded_password")
                    .name("테스터")
                    .build();

            DeleteMemberRequest request = new DeleteMemberRequest("password");

            when(memberRepository.findByMemberIdAndIsDeletedFalse(currentMemberId)).thenReturn(Optional.of(member));
            when(passwordEncoder.matches("password", "encoded_password")).thenReturn(true);

            // when
            memberService.deleteMember(request);

            // then
            assertThat(member.getIsDeleted()).isTrue();
            assertThat(member.getDeletedAt()).isNotNull();
        }

        @Test
        @WithMockUser(username = "1")
        @DisplayName("실패 - 비밀번호 불일치")
        void fail_password_mismatch() {

            // given
            Long currentMemberId = 1L;
            Member member = Member.builder()
                    .memberId(currentMemberId)
                    .nickname("user")
                    .password("encoded_password")
                    .name("테스터")
                    .build();

            DeleteMemberRequest request = new DeleteMemberRequest("wrong_password");

            when(memberRepository.findByMemberIdAndIsDeletedFalse(currentMemberId)).thenReturn(Optional.of(member));
            when(passwordEncoder.matches("wrong_password", "encoded_password")).thenReturn(false);

            assertThrows(PasswordMismatchException.class, () -> memberService.deleteMember(request));
            assertThat(member.getIsDeleted()).isFalse();

        }

        @Test
        @WithMockUser(username = "1")
        @DisplayName("실패 - 존재하지 않은 사용자")
        void fail_member_not_found() {

            // given
            Long currentMemberId = 1L;
            DeleteMemberRequest request = new DeleteMemberRequest("password");

            when(memberRepository.findByMemberIdAndIsDeletedFalse(currentMemberId)).thenReturn(Optional.empty());

            assertThrows(MemberNotFoundException.class, () -> memberService.deleteMember(request));
        }
    }
}
