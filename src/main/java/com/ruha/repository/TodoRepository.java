package com.ruha.repository;

import com.ruha.entity.Member;
import com.ruha.entity.Todo;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {

    long countByMember(Member member);

    long countByMemberAndIsCompleted(Member member, boolean isCompleted);

}
