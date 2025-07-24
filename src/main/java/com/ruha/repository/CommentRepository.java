package com.ruha.repository;

import com.ruha.entity.Member;
import com.ruha.entity.Todo;
import com.ruha.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByTodo(Todo todo);

    List<Comment> findByMember(Member member);

    long countByMember(Member member);
}
