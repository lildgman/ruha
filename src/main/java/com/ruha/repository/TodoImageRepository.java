package com.ruha.repository;

import com.ruha.entity.TodoImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoImageRepository extends JpaRepository<TodoImage, Long> {
}
