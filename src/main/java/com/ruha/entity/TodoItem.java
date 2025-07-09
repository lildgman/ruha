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
    private Long id;

    private String content;
    private Boolean isCompleted;

    @Enumerated(EnumType.STRING)
    private Importance importance;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime created;

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
