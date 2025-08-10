package com.ruha.service;

import com.ruha.dto.Todo.CreateTodoRequest;
import com.ruha.dto.Todo.CreateTodoResponse;
import com.ruha.entity.*;
import com.ruha.repository.CategoryRepository;
import com.ruha.repository.MemberRepository;
import com.ruha.repository.TodoImageRepository;
import com.ruha.repository.TodoRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
class TodoServiceTest {

    @Autowired
    private TodoService todoService;

    @MockBean
    private TodoRepository todoRepository;

    @MockBean
    private MemberRepository memberRepository;

    @MockBean
    private CategoryRepository categoryRepository;

    @MockBean
    private TodoImageRepository todoImageRepository;

    @MockBean
    private FileService fileService;


    @Nested
    @DisplayName("투두 생성")
    class CreateTodo {

        @Test
        @WithMockUser(username = "1")
        @DisplayName("성공 - 이미지 없음")
        void 투두생성_성공_이미지x() {

            // given
            CreateTodoRequest request = new CreateTodoRequest(
                    "테스트 투두 제목",
                    "테스트 투두 설명",
                    Importance.HIGH,
                    true,
                    1L
            );

            Member member = createMember(1L, "tester");
            Category category = createCategory(1L, "카테고리1");
            Todo savedTodo = createTodo(request, 1L, member, category);

            when(memberRepository.findByMemberIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(member));
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
            when(todoRepository.save(any(Todo.class))).thenReturn(savedTodo);

            // when
            CreateTodoResponse response = todoService.createTodo(request, null);

            // then
            assertThat(response.getTodoId()).isEqualTo(1L);
            assertThat(response.getTitle()).isEqualTo(response.getTitle());
            assertThat(response.getDescription()).isEqualTo(response.getDescription());
            assertThat(response.getIsPublic()).isEqualTo(response.getIsPublic());
            assertThat(response.getIsCompleted()).isFalse();
            assertThat(response.getCategoryId()).isEqualTo(request.getCategoryId());
            assertThat(response.getCategoryName()).isEqualTo(category.getName());
            assertThat(response.getMemberId()).isEqualTo(member.getMemberId());
            assertThat(response.getMemberNickname()).isEqualTo(member.getNickname());

            verify(memberRepository, times(1)).findByMemberIdAndIsDeletedFalse(1L);
            verify(categoryRepository, times(1)).findById(1L);
            verify(todoRepository, times(1)).save(any(Todo.class));
            verify(todoImageRepository, never()).saveAll(any());
        }
    }

    private Member createMember(Long id, String nickname) {
        return Member.builder()
                .memberId(id)
                .nickname(nickname)
                .name("테스터")
                .password("encoded_password")
                .build();
    }

    private Category createCategory(Long id, String name) {

        return Category.builder()
                .categoryId(id)
                .name(name)
                .build();
    }

    private Todo createTodo(CreateTodoRequest request, Long todoId, Member member, Category category) {
        return Todo.builder()
                .todoId(todoId)
                .title(request.getTitle())
                .description(request.getDescription())
                .importance(request.getImportance())
                .isPublic(request.getIsPublic())
                .isCompleted(false)
                .member(member)
                .category(category)
                .build();
    }

    private TodoImage createTodoImage(Long id, String fileName, String filePath, Todo todo) {

        return TodoImage.builder()
                .todoImageId(id)
                .fileName(fileName)
                .filePath(filePath)
                .todo(todo)
                .build();
    }

}