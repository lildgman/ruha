package com.ruha.repository;

import com.ruha.entity.Category;
import com.ruha.entity.Importance;
import com.ruha.entity.Member;
import com.ruha.entity.Todo;
import com.ruha.entity.TodoImage;
import com.ruha.exception.todo.TodoErrorCode;
import com.ruha.exception.todo.TodoNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional
class TodoRepositoryTest {

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TodoImageRepository todoImageRepository;

    private Member member;
    private Category category;

    @BeforeEach
    void setUp() {
        member = Member.builder()
                .email("test@example.com")
                .password("1234")
                .name("test")
                .build();
        memberRepository.save(member);

        category = Category.builder()
                .name("카테고리1")
                .build();
        categoryRepository.save(category);
    }

    private Todo createTodo() {
        return Todo.builder()
                .title("테스트")
                .description("테스트 설명")
                .isPublic(true)
                .importance(Importance.LOW)
                .member(member)
                .category(category)
                .build();
    }

    @Test
    void 투두_저장() {
        // given
        Todo todo = createTodo();

        // when
        todoRepository.save(todo);

        // then
        Todo foundTodo = todoRepository.findById(todo.getTodoId())
                .orElseThrow(() -> new TodoNotFoundException(TodoErrorCode.TODO_NOT_FOUND));

        assertThat(foundTodo.getTodoId()).isEqualTo(todo.getTodoId());
        assertThat(foundTodo.getTitle()).isEqualTo("테스트");
        assertThat(foundTodo.getMember().getName()).isEqualTo("test");
    }

    @Test
    void 이미지와_함께_투두_저장() {
        // given
        Todo todo = createTodo();
        TodoImage image1 = TodoImage.builder().fileName("image1.jpg").filePath("/path/to/image1.jpg").build();
        TodoImage image2 = TodoImage.builder().fileName("image2.png").filePath("/path/to/image2.png").build();

        todo.addTodoImage(image1);
        todo.addTodoImage(image2);

        // when
        todoRepository.save(todo);

        // then
        Todo foundTodo = todoRepository.findById(todo.getTodoId())
                .orElseThrow(() -> new TodoNotFoundException(TodoErrorCode.TODO_NOT_FOUND));

        assertThat(foundTodo.getTodoImages()).hasSize(2);
        assertThat(foundTodo.getTodoImages().get(0).getFileName()).isEqualTo("image1.jpg");

        List<TodoImage> images = todoImageRepository.findAll();
        assertThat(images).hasSize(2);
    }

    @Test
    void 투두_조회() {
        // given
        Todo savedTodo = todoRepository.save(createTodo());

        // when
        Todo foundTodo = todoRepository.findById(savedTodo.getTodoId())
                .orElseThrow(() -> new TodoNotFoundException(TodoErrorCode.TODO_NOT_FOUND));

        // then
        assertThat(foundTodo.getTodoId()).isEqualTo(savedTodo.getTodoId());
    }

    @Test
    void 투두_수정() {
        // given
        Todo savedTodo = todoRepository.save(createTodo());

        // when
        savedTodo.updateTitle("수정된 타이틀");
        todoRepository.flush();

        // then
        Todo foundTodo = todoRepository.findById(savedTodo.getTodoId())
                .orElseThrow(() -> new TodoNotFoundException(TodoErrorCode.TODO_NOT_FOUND));
        assertThat(foundTodo.getTitle()).isEqualTo("수정된 타이틀");
    }

    @Test
    void 투두_삭제() {
        // given
        Todo savedTodo = todoRepository.save(createTodo());

        // when
        todoRepository.delete(savedTodo);

        // then
        assertThat(todoRepository.findById(savedTodo.getTodoId())).isEmpty();
    }
}