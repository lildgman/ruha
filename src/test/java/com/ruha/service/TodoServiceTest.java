package com.ruha.service;

import com.ruha.dto.Todo.CreateTodoRequest;
import com.ruha.dto.Todo.CreateTodoResponse;
import com.ruha.entity.*;
import com.ruha.exception.auth.UnauthorizedException;
import com.ruha.exception.category.CategoryNotFoundException;
import com.ruha.repository.CategoryRepository;
import com.ruha.repository.MemberRepository;
import com.ruha.repository.TodoImageRepository;
import com.ruha.repository.TodoRepository;
import com.ruha.util.SecurityUtil;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
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

        @Test
        @WithMockUser(username = "1")
        @DisplayName("성공 - 이미지 포함")
        void 투두생성_성공_이미지o() {

            // given
            Member member = createMember(1L, "테스터");
            Category category = createCategory(1L, "카테고리");

            CreateTodoRequest request = new CreateTodoRequest(
                    "테스트 제목",
                    "테스트 내용",
                    Importance.HIGH,
                    true,
                    1L
            );
            Todo savedTodo = createTodo(request, 1L, member, category);

            MultipartFile img1 = mock(MultipartFile.class);
            MultipartFile img2 = mock(MultipartFile.class);
            List<MultipartFile> images = Arrays.asList(img1, img2);

            TodoImage todoImage1 = createTodoImage(1L, "image1.jpg", "/path/image1.jpg", savedTodo);
            TodoImage todoImage2 = createTodoImage(2L, "image2.jpg", "/path/image2.jpg", savedTodo);
            List<TodoImage> savedImages = Arrays.asList(todoImage1, todoImage2);

            when(memberRepository.findByMemberIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(member));
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
            when(todoRepository.save(any(Todo.class))).thenReturn(savedTodo);
            when(img1.isEmpty()).thenReturn(false);
            when(img2.isEmpty()).thenReturn(false);
            when(img1.getOriginalFilename()).thenReturn("image1.jpg");
            when(img2.getOriginalFilename()).thenReturn("image2.jpg");
            when(fileService.saveFile(img1, "todos/1")).thenReturn("/path/image1.jpg");
            when(fileService.saveFile(img2, "todos/1")).thenReturn("/path/image2.jpg");
            when(todoImageRepository.saveAll(any())).thenReturn(savedImages);

            // when
            CreateTodoResponse response = todoService.createTodo(request, images);

            // then
            assertThat(response.getTodoId()).isEqualTo(1L);
            assertThat(response.getTodoImages()).hasSize(2);
            assertThat(response.getTodoImages().get(0).getFileName()).isEqualTo("image1.jpg");
            assertThat(response.getTodoImages().get(1).getFileName()).isEqualTo("image2.jpg");

            verify(fileService, times(2)).saveFile(any(), anyString());
            verify(todoImageRepository, times(1)).saveAll(any());
        }

        @Test
        @DisplayName("실패 - 인증되지 않은 사용자")
        void 투두생성_실패_인증되지_않은_사용자() {

            // given
            CreateTodoRequest request = new CreateTodoRequest(
                    "테스트 제목",
                    "테스트 내용",
                    Importance.HIGH,
                    true,
                    1L
            );

            try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
                mockedSecurityUtil.when(SecurityUtil::getLoginMemberId)
                        .thenReturn(Optional.empty());
            }

            // then
            assertThrows(UnauthorizedException.class, () -> todoService.createTodo(request, null));

            verify(memberRepository, never()).findByMemberIdAndIsDeletedFalse(any());
            verify(todoRepository, never()).save(any());
        }

        @Test
        @WithMockUser(username = "1")
        @DisplayName("실패 - 없는 카테고리")
        void 투두생성_실패_카테고리x() {

            // given
            CreateTodoRequest request = new CreateTodoRequest(
                    "테스트 제목",
                    "테스트 내용",
                    Importance.HIGH,
                    true,
                    999L
            );

            Member member = createMember(1L, "테스터");

            when(memberRepository.findByMemberIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(member));
            when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(CategoryNotFoundException.class,
                    () -> todoService.createTodo(request, null));

            verify(memberRepository, times(1)).findByMemberIdAndIsDeletedFalse(1L);
            verify(categoryRepository, times(1)).findById(999L);
            verify(todoRepository, never()).save(any());
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