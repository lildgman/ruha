package com.ruha.repository;

import com.ruha.dto.member.CreateMemberRequest;
import com.ruha.entity.Category;
import com.ruha.entity.Member;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class TodoRepositoryTest {

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Member member;
    private Category category;

    @BeforeEach
    void setUp() {
        CreateMemberRequest request = new CreateMemberRequest("test@example.com", "1234", "test");
        member = Member.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .name(request.getName())
                .build();

        category = Category.builder()
                .name("카테고리1")
                .build();
    }


}