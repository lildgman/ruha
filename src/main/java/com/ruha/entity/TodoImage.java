package com.ruha.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class TodoImage extends BaseCreatedAtEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long todoImageId;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String filePath;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "todo_id", nullable = false)
    private Todo todo;

    public void updateTodo(Todo todo) {
        this.todo = todo;
    }
}
