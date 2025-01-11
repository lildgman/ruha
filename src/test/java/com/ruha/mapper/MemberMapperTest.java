package com.ruha.mapper;

import com.ruha.entity.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@MapperScan(basePackages = "com.ruha.mapper")
class MemberMapperTest {

    @Autowired
    private MemberMapper memberMapper;

    @Test
    void testFindAll() {

        // given
        Member member = new Member("test@gmail.com", "1234", "test");

        // when
        memberMapper.insertMember(member);

        // then
        List<Member> members = memberMapper.findAll();
        assertThat(members).isNotEmpty();

    }

}