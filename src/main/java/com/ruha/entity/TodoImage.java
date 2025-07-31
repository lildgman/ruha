package com.ruha.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class TodoImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long todoImageId;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String filePath;

    @Column(nullable = false)
    private LocalDateTime created;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "todo_id", nullable = false)
    private Todo todo;

    @PrePersist
    public void prePersist() {
        this.created = LocalDateTime.now();
    }

    public void updateTodo(Todo todo) {
        this.todo = todo;
    }
}
