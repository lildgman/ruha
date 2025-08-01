package com.ruha.repository;

import com.ruha.entity.*;
import com.ruha.entity.Comment;
import com.ruha.exception.comment.CommentNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

    @Test
    @DisplayName("특정 회원이 작성한 댓글 수 조회")
    void 댓글_수_조회() {

        // given
        commentRepository.save(createComment(member1, todo, "회원1 댓글1"));
        commentRepository.save(createComment(member1, todo, "회원1 댓글2"));
        commentRepository.save(createComment(member1, todo, "회원1 댓글3"));
        // when
        long count = commentRepository.countByMember(member1);

        // then
        assertThat(count).isEqualTo(3);
    }

    @Test
    @DisplayName("댓글이 없는 회원의 댓글 수는 0")
    void 댓글이_없는_회원의_댓글수() {

        // given
        Member member = Member.builder()
                .nickname("nocomment")
                .password("1234")
                .name("회원")
                .build();
        memberRepository.save(member);

        // when
        long count = commentRepository.countByMember(member);

        // then
        assertThat(count).isZero();

    }

    @Test
    @DisplayName("댓글이 없는 Todo의 댓글 목록은 빈 리스트")
    void 댓글이_없는_todo의_댓글목록() {

        // given
        Category category = categoryRepository.save(
                Category.builder().name("카테고리").build()
        );

        Todo todo = Todo.builder()
                .title("댓글 없는 투두")
                .description("테스트")
                .isPublic(true)
                .importance(Importance.HIGH)
                .member(member1)
                .category(category)
                .build();

        todoRepository.save(todo);

        // when
        List<Comment> comments = commentRepository.findByTodo(todo);

        // then
        assertThat(comments).isEmpty();

    }

    @Test
    @DisplayName("댓글 저장 시 생성시간, 수정시간 설정")
    void 댓글_저장_시_생성시간_수정시간_설정() {

        // given
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);
        Comment comment = createComment(member1, todo, "테스트");

        // when
        Comment savedComment = commentRepository.save(comment);
        LocalDateTime after = LocalDateTime.now().plusSeconds(1);

        // then
        assertThat(savedComment.getCreatedAt()).isNotNull();
        assertThat(savedComment.getUpdatedAt()).isNotNull();
        assertThat(savedComment.getCreatedAt()).isBetween(before, after);
        assertThat(savedComment.getUpdatedAt()).isBetween(before, after);

    }

    @Test
    @DisplayName("댓글 수정 시 수정시간 업데이트")
    void 댓글_수정_시_수정시간_업데이트() throws InterruptedException {

        // given
        Comment savedComment = commentRepository.save(createComment(member1, todo, "before"));
        LocalDateTime beforeUpdatedAt = savedComment.getUpdatedAt();

        Thread.sleep(100);

        // when
        savedComment.updateContent("after");
        commentRepository.flush();

        // then
        Comment updatedComment = commentRepository.findById(savedComment.getCommentId())
                .orElseThrow(CommentNotFoundException::new);
        assertThat(updatedComment.getUpdatedAt()).isAfter(beforeUpdatedAt);

    }

    @Test
    @DisplayName("댓글 목록이 생성시간 순으로 정렬")
    void 댓글목록_정렬() throws InterruptedException {

        // given
        Comment comment1 = commentRepository.save(createComment(member1, todo, "1"));
        Thread.sleep(50);
        Comment comment2 = commentRepository.save(createComment(member1, todo, "2"));
        Thread.sleep(50);
        Comment comment3 = commentRepository.save(createComment(member1, todo, "3"));

        // when
        List<Comment> comments = commentRepository.findByTodo(todo);

        // then
        assertThat(comments).hasSize(3);
        assertThat(comments.get(0)).isEqualTo(comment1);
        assertThat(comments.get(1)).isEqualTo(comment2);
        assertThat(comments.get(2)).isEqualTo(comment3);
    }

    @Test
    @DisplayName("탈퇴한 회원의 댓글도 삭제")
    void 탈퇴한_회원의_댓글도_삭제() {
        Member testMember = Member.builder()
                .nickname("deletetest")
                .password("1234")
                .name("삭제테스트")
                .build();
        memberRepository.save(testMember);

        Category testCategory = categoryRepository.save(
                Category.builder().name("삭제테스트카테고리").build());

        Todo testTodo = Todo.builder()
                .title("삭제테스트 투두")
                .description("테스트")
                .isPublic(false)
                .importance(Importance.LOW)
                .member(testMember)
                .category(testCategory)
                .build();
        todoRepository.save(testTodo);

        Comment comment = Comment.builder()
                .content("삭제될 댓글")
                .member(testMember)
                .todo(testTodo)
                .build();
        commentRepository.save(comment);
        testMember.addComment(comment);
        Long commentId = comment.getCommentId();

        // when
        memberRepository.delete(testMember);

        // then
        assertThat(commentRepository.findById(commentId)).isEmpty();


    }
}