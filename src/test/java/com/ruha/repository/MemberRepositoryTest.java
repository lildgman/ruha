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
                .nickname("testUser")
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
        assertThat(saved.getNickname()).isEqualTo(member.getNickname());
        assertThat(saved.getPassword()).isEqualTo("1234");
    }

    @Test
    @DisplayName("회원가입 실패 - 중복 닉네임)")
    void 회원가입_실패_중복닉네임() {
        // given
        memberRepository.save(member); // 먼저 기준이 될 회원을 저장

        Member duplicateMember = Member.builder()
                .nickname(member.getNickname()) // 동일한 이메일
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
                .orElseThrow(MemberNotFoundException::new);

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
        savedMember.delete();
        memberRepository.save(savedMember);


        // then
        assertThat(savedMember.getIsDeleted()).isTrue();
        assertThat(savedMember.getDeletedAt()).isNotNull();

        assertThat(memberRepository.existsById(savedMember.getMemberId())).isTrue();

        assertThat(memberRepository.findByMemberIdAndIsDeletedFalse(savedMember.getMemberId())).isEmpty();
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
                .orElseThrow(MemberNotFoundException::new);
        assertThat(updatedMember.getName()).isEqualTo("updateTest");

    }

    @Test
    @DisplayName("활성 회원만 닉네임으로 조회")
    void 활성_회원만_닉네임으로_조회() {
        // given
        Member savedMember = memberRepository.save(member);
        
        // when & then - 활성 회원 조회 성공
        assertThat(memberRepository.findByNicknameAndIsDeletedFalse(member.getNickname()))
                .isPresent()
                .get()
                .extracting(Member::getMemberId)
                .isEqualTo(savedMember.getMemberId());
        
        // 회원 탈퇴
        savedMember.delete();
        memberRepository.save(savedMember);
        
        // when & then - 탈퇴한 회원은 조회되지 않음
        assertThat(memberRepository.findByNicknameAndIsDeletedFalse(member.getNickname()))
                .isEmpty();
    }

    @Test
    @DisplayName("활성 회원만 ID로 조회")
    void 활성_회원만_ID로_조회() {
        // given
        Member savedMember = memberRepository.save(member);
        
        // when & then - 활성 회원 조회 성공
        assertThat(memberRepository.findByMemberIdAndIsDeletedFalse(savedMember.getMemberId()))
                .isPresent()
                .get()
                .extracting(Member::getNickname)
                .isEqualTo(member.getNickname());
        
        // 회원 탈퇴
        savedMember.delete();
        memberRepository.save(savedMember);
        
        // when & then - 탈퇴한 회원은 조회되지 않음
        assertThat(memberRepository.findByMemberIdAndIsDeletedFalse(savedMember.getMemberId()))
                .isEmpty();
    }

    @Test
    @DisplayName("활성 회원만 닉네임 존재 여부 확인")
    void 활성_회원만_닉네임_존재여부_확인() {
        // given
        Member savedMember = memberRepository.save(member);
        
        // when & then - 활성 회원 존재 확인
        assertThat(memberRepository.existsByNicknameAndIsDeletedFalse(member.getNickname()))
                .isTrue();
        
        // 회원 탈퇴
        savedMember.delete();
        memberRepository.save(savedMember);
        
        // when & then - 탈퇴한 회원은 존재하지 않는 것으로 처리
        assertThat(memberRepository.existsByNicknameAndIsDeletedFalse(member.getNickname()))
                .isFalse();
        
        // 하지만 전체 회원에서는 여전히 존재 (닉네임 중복 방지용)
        assertThat(memberRepository.existsByNickname(member.getNickname()))
                .isTrue();
    }

    @Test
    @DisplayName("탈퇴한 회원과 동일한 닉네임으로 회원가입 시도 - 실패")
    void 탈퇴한_회원과_동일한_닉네임으로_회원가입_시도_실패() {
        // given
        Member savedMember = memberRepository.save(member);
        savedMember.delete();
        memberRepository.save(savedMember);
        
        Member newMember = Member.builder()
                .nickname(member.getNickname()) // 탈퇴한 회원과 동일한 닉네임
                .password("newPassword")
                .name("newUser")
                .build();
        
        // when & then - 탈퇴한 회원의 닉네임이라도 중복으로 처리되어 실패
        assertThatThrownBy(() -> memberRepository.saveAndFlush(newMember))
                .isInstanceOf(org.springframework.dao.DataIntegrityViolationException.class);
    }

}
