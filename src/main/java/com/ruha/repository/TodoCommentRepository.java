package com.ruha.repository;

import com.ruha.entity.Member;
import com.ruha.entity.Todo;
import com.ruha.entity.TodoComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TodoCommentRepository extends JpaRepository<TodoComment, Long> {

    List<TodoComment> findByTodo(Todo todo);

    List<TodoComment> findByMember(Member member);
}
