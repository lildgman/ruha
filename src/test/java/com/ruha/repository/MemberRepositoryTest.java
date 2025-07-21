package com.ruha.repository;

import com.ruha.entity.Member;
import com.ruha.exception.member.MemberErrorCode;
import com.ruha.exception.member.MemberNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    private Member member;

    @BeforeEach
    void memberSetUp() {
        member = Member.builder()
                .nickname("test@example.com")
                .password("1234")
                .name("test")
                .build();
    }

    @Test
    @DisplayName("회원가입")
    void 회원가입() {

        // given

        // when
        Member saved = memberRepository.save(member);

        //then
        assertThat(saved.getNickname()).isEqualTo("test@example.com");
        assertThat(saved.getPassword()).isEqualTo("1234");
    }

    @Test
    @DisplayName("회원가입 - 실패 (중복된 이메일)")
    void 회원가입_실패_중복이메일() {
        // given
        memberRepository.save(member); // 먼저 기준이 될 회원을 저장

        Member duplicateMember = Member.builder()
                .nickname("test@example.com") // 동일한 이메일
                .password("5678")
                .name("duplicate")
                .build();

        // when & then
        // 동일한 이메일로 저장 시도 시, 데이터베이스의 unique 제약 조건 위반으로 예외가 발생해야 함
        // JPA는 DB 예외를 DataIntegrityViolationException으로 변환하여 던져줌
        assertThatThrownBy(() -> memberRepository.saveAndFlush(duplicateMember))
                .isInstanceOf(org.springframework.dao.DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("회원조회")
    void 회원조회() {

        // given
        Member savedMember = memberRepository.save(member);

        // when
        Member findMember = memberRepository.findById(savedMember.getMemberId())
                .orElseThrow(() -> new MemberNotFoundException(MemberErrorCode.MEMBER_NOT_FOUND));

        // then
        assertThat(findMember.getMemberId()).isEqualTo(savedMember.getMemberId());
        assertThat(findMember.getNickname()).isEqualTo(savedMember.getNickname());

    }

    @Test
    @DisplayName("회원탈퇴")
    void 회원탈퇴() {
        // given
        Member savedMember = memberRepository.save(member);

        // when
        memberRepository.delete(savedMember);

        // then
        assertThat(memberRepository.existsById(savedMember.getMemberId())).isFalse();
        assertThatThrownBy(() -> memberRepository.findById(savedMember.getMemberId())
                .orElseThrow(() -> new MemberNotFoundException(MemberErrorCode.MEMBER_NOT_FOUND)))
                .isInstanceOf(MemberNotFoundException.class)
                .hasMessage("회원이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("회원수정")
    void 회원수정() {
        // given
        Member savedMember = memberRepository.save(member);

        // when
        savedMember.updateName("updateTest");

        // then
        Member updatedMember = memberRepository.findById(savedMember.getMemberId())
                .orElseThrow(() -> new MemberNotFoundException(MemberErrorCode.MEMBER_NOT_FOUND));
        assertThat(updatedMember.getName()).isEqualTo("updateTest");

    }

}
