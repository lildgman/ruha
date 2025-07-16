package com.ruha.repository;

import com.ruha.entity.Member;
import com.ruha.exception.MemberErrorCode;
import com.ruha.exception.MemberNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    private Member member;

    @BeforeEach
    void memberSetUp() {
        member = Member.builder()
                .email("test@example.com")
                .password("1234")
                .name("test")
                .build();
    }

    @Test
    void 회원가입() {

        // given

        // when
        Member saved = memberRepository.save(member);

        //then
        assertThat(saved.getEmail()).isEqualTo("test@example.com");
        assertThat(saved.getPassword()).isEqualTo("1234");
    }

    @Test
    void 회원조회() {

        // given
        Member savedMember = memberRepository.save(member);

        // when
        Member findMember = memberRepository.findById(savedMember.getMemberId())
                .orElseThrow(() -> new MemberNotFoundException(MemberErrorCode.MEMBER_NOT_FOUND));

        // then
        assertThat(findMember.getMemberId()).isEqualTo(savedMember.getMemberId());

    }

    @Test
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