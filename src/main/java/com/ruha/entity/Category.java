package com.ruha.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Category extends BaseCreatedAtEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long categoryId;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Todo> todos = new ArrayList<>();

    public void addTodo(Todo todo) {
        if (todo == null) return;
        
        this.todos.add(todo);
        if (todo.getCategory() != this) {
            todo.updateCategory(this);
        }
    }

    public void removeTodo(Todo todo) {
        if (todo == null) return;
        
        this.todos.remove(todo);
        if (todo.getCategory() == this) {
            todo.updateCategory(null);
        }
    }

}
