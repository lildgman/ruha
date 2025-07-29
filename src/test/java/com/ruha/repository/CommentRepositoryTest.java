package com.ruha.repository;

import com.ruha.entity.*;
import com.ruha.entity.Comment;
import com.ruha.exception.comment.CommentErrorCode;
import com.ruha.exception.comment.CommentNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

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
                .nickname("user1")
                .password("1234")
                .name("user1")
                .build();

        member2 = Member.builder()
                .nickname("user2")
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

    private Comment createComment(Member member, Todo todo, String content) {
        return Comment.builder()
                .content(content)
                .member(member)
                .todo(todo)
                .build();
    }

    @Test
    @DisplayName("댓글 저장")
    void 댓글_저장() {
        // given
        Comment comment = createComment(member1, todo, "첫 번째 댓글입니다.");

        // when
        Comment savedComment = commentRepository.save(comment);

        // then
        assertThat(savedComment.getCommentId()).isNotNull();
        assertThat(savedComment.getContent()).isEqualTo("첫 번째 댓글입니다.");
        assertThat(savedComment.getMember()).isEqualTo(member1);
        assertThat(savedComment.getTodo()).isEqualTo(todo);
    }

    @Test
    @DisplayName("댓글 조회")
    void 댓글_조회() {
        // given
        Comment savedComment = commentRepository.save(createComment(member1, todo, "댓글 조회 테스트"));

        // when
        Comment foundComment = commentRepository.findById(savedComment.getCommentId())
                .orElseThrow(CommentNotFoundException::new);

        // then
        assertThat(foundComment).isEqualTo(savedComment);
    }

    @Test
    @DisplayName("댓글 수정")
    void 댓글_수정() {
        // given
        Comment savedComment = commentRepository.save(createComment(member1, todo, "수정 전 댓글"));

        // when
        savedComment.updateContent("수정된 댓글입니다.");
        commentRepository.flush(); // 변경 감지(dirty checking)를 위해 flush

        // then
        Comment updatedComment = commentRepository.findById(savedComment.getCommentId())
                .orElseThrow(CommentNotFoundException::new);
        assertThat(updatedComment.getContent()).isEqualTo("수정된 댓글입니다.");
    }

    @Test
    @DisplayName("댓글 삭제")
    void 댓글_삭제() {
        // given
        Comment savedComment = commentRepository.save(createComment(member1, todo, "삭제될 댓글"));

        // when
        commentRepository.delete(savedComment);

        // then
        assertThat(commentRepository.findById(savedComment.getCommentId())).isEmpty();
    }

    @Test
    @DisplayName("특정 TODO의 댓글 목록 조회")
    void 특정_Todo의_댓글_목록_조회() {
        // given
        commentRepository.save(createComment(member1, todo, "댓글 1"));
        commentRepository.save(createComment(member2, todo, "댓글 2"));

        // when
        List<Comment> comments = commentRepository.findByTodo(todo);

        // then
        assertThat(comments).hasSize(2);
        assertThat(comments).extracting("content").containsExactlyInAnyOrder("댓글 1", "댓글 2");
    }

    @Test
    @DisplayName("특정 Member가 작성한 댓글 목록 조회")
    void 특정_Member가_작성한_댓글_목록_조회() {
        // given
        commentRepository.save(createComment(member1, todo, "멤버1의 첫 댓글"));
        commentRepository.save(createComment(member2, todo, "멤버2의 유일한 댓글"));
        commentRepository.save(createComment(member1, todo, "멤버1의 두 번째 댓글"));

        // when
        List<Comment> comments = commentRepository.findByMember(member1);

        // then
        assertThat(comments).hasSize(2);
        assertThat(comments).extracting("member").containsOnly(member1);
    }
}