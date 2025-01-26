package com.ruha.repository;

import com.ruha.dto.CreateMemberRequest;
import com.ruha.entity.Member;
import com.ruha.entity.RoleType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void createMember() {

        // given
        CreateMemberRequest request = new CreateMemberRequest("test@example.com", "1234", "test");
        Member member = Member.toEntity(request);
        // when
        Member saved = memberRepository.save(member);

        //then
        assertThat(saved.getEmail()).isEqualTo("test@example.com");
        assertThat(saved.getPassword()).isEqualTo("1234");
    }

    @Test
    void findMember() {

        // given
        CreateMemberRequest request = new CreateMemberRequest("test@example.com", "1234", "test");
        Member member = Member.toEntity(request);

        // when
        Member saved = memberRepository.save(member);
        Member findMember = memberRepository.findById(saved.getId())
                .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));

        // then
        assertThat(findMember.getId()).isEqualTo(saved.getId());
        assertThat(findMember.getRoleType()).isEqualTo(RoleType.NORMAL);
    }


}