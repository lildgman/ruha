package com.ruha.repository;

import com.ruha.dto.CreateMemberRequest;
import com.ruha.entity.Member;
import com.ruha.entity.RoleType;
import com.ruha.exception.MemberNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    private Member member;
    @BeforeEach
    void memberSetUp() {
        CreateMemberRequest request = new CreateMemberRequest("test@example.com", "1234", "test");
        member = Member.toEntity(request);
    }

    @Test
    void createMember() {

        // given

        // when
        Member saved = memberRepository.save(member);

        //then
        assertThat(saved.getEmail()).isEqualTo("test@example.com");
        assertThat(saved.getPassword()).isEqualTo("1234");
    }

    @Test
    void findMember() {

        // given

        // when
        Member saved = memberRepository.save(member);
        Member findMember = memberRepository.findById(saved.getId())
                .orElseThrow(() -> new MemberNotFoundException("계정이 존재하지 않습니다."));

        // then
        assertThat(findMember.getId()).isEqualTo(saved.getId());
        assertThat(findMember.getRoleType()).isEqualTo(RoleType.NORMAL);

    }

}