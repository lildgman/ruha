package com.ruha.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class TodoItem {

    @Id
    @GeneratedValue
    private Long todoItemId;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private Boolean isCompleted;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Importance importance;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime created;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime updated;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "todoList_id")
    private TodoList todoList;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.importance = Importance.MIDDLE;
        this.created = now;
        this.updated = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.updated = LocalDateTime.now();
    }

}
