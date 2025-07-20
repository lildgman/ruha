package com.ruha.repository;

import com.ruha.entity.Category;
import com.ruha.entity.Importance;
import com.ruha.entity.Member;
import com.ruha.entity.Todo;
import com.ruha.entity.TodoComment;
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
class TodoCommentRepositoryTest {

    @Autowired
    private TodoCommentRepository todoCommentRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Member member1;
    private Member member2;
    private Todo todo;

    @BeforeEach
    void setUp() {
        member1 = Member.builder()
                .email("user1@example.com")
                .password("1234")
                .name("user1")
                .build();

        member2 = Member.builder()
                .email("user2@example.com")
                .password("1234")
                .name("user2")
                .build();

        memberRepository.save(member1);
        memberRepository.save(member2);

        Category category = categoryRepository.save(
                Category.builder()
                        .name("일상")
                        .build());

        todo = Todo.builder()
                .title("오늘의 할 일")
                .description("테스트 코드 작성하기")
                .isPublic(true)
                .importance(Importance.HIGH)
                .member(member1)
                .category(category)
                .build();

        todoRepository.save(todo);
    }

    private TodoComment createComment(Member member, Todo todo, String content) {
        return TodoComment.builder()
                .content(content)
                .member(member)
                .todo(todo)
                .build();
    }

    @Test
    void 댓글_저장() {
        // given
        TodoComment comment = createComment(member1, todo, "첫 번째 댓글입니다.");

        // when
        TodoComment savedComment = todoCommentRepository.save(comment);

        // then
        assertThat(savedComment.getTodoCommentId()).isNotNull();
        assertThat(savedComment.getContent()).isEqualTo("첫 번째 댓글입니다.");
        assertThat(savedComment.getMember()).isEqualTo(member1);
        assertThat(savedComment.getTodo()).isEqualTo(todo);
    }

    @Test
    void 댓글_조회() {
        // given
        TodoComment savedComment = todoCommentRepository.save(createComment(member1, todo, "댓글 조회 테스트"));

        // when
        TodoComment foundComment = todoCommentRepository.findById(savedComment.getTodoCommentId()).orElseThrow();

        // then
        assertThat(foundComment).isEqualTo(savedComment);
    }

    @Test
    void 댓글_수정() {
        // given
        TodoComment savedComment = todoCommentRepository.save(createComment(member1, todo, "수정 전 댓글"));

        // when
        savedComment.updateContent("수정된 댓글입니다.");
        todoCommentRepository.flush(); // 변경 감지(dirty checking)를 위해 flush

        // then
        TodoComment updatedComment = todoCommentRepository.findById(savedComment.getTodoCommentId()).orElseThrow();
        assertThat(updatedComment.getContent()).isEqualTo("수정된 댓글입니다.");
    }

    @Test
    void 댓글_삭제() {
        // given
        TodoComment savedComment = todoCommentRepository.save(createComment(member1, todo, "삭제될 댓글"));

        // when
        todoCommentRepository.delete(savedComment);

        // then
        assertThat(todoCommentRepository.findById(savedComment.getTodoCommentId())).isEmpty();
    }

    @Test
    void 특정_Todo의_댓글_목록_조회() {
        // given
        todoCommentRepository.save(createComment(member1, todo, "댓글 1"));
        todoCommentRepository.save(createComment(member2, todo, "댓글 2"));

        // when
        List<TodoComment> comments = todoCommentRepository.findByTodo(todo);

        // then
        assertThat(comments).hasSize(2);
        assertThat(comments).extracting("content").containsExactlyInAnyOrder("댓글 1", "댓글 2");
    }

    @Test
    void 특정_Member가_작성한_댓글_목록_조회() {
        // given
        todoCommentRepository.save(createComment(member1, todo, "멤버1의 첫 댓글"));
        todoCommentRepository.save(createComment(member2, todo, "멤버2의 유일한 댓글"));
        todoCommentRepository.save(createComment(member1, todo, "멤버1의 두 번째 댓글"));

        // when
        List<TodoComment> comments = todoCommentRepository.findByMember(member1);

        // then
        assertThat(comments).hasSize(2);
        assertThat(comments).extracting("member").containsOnly(member1);
    }
}