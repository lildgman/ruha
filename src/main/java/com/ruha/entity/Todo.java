package com.ruha.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Todo extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long todoId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Boolean isCompleted;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Importance importance;

    @Column(nullable = false)
    private Boolean isPublic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "todo_id")
    @Builder.Default
    private List<TodoImage> todoImages = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "todo_id")
    @Builder.Default
    private List<TodoComment> todoComments = new ArrayList<>();


    @PrePersist
    public void prePersist() {
        this.isCompleted = false;
    }

    //== 연관관계 편의 메소드 ==//
    public void addTodoImage(TodoImage todoImage) {
        this.todoImages.add(todoImage);
    }

    public void removeTodoImage(TodoImage todoImage) {
        this.todoImages.remove(todoImage);
    }

    public void addTodoComment(TodoComment todoComment) {
        this.todoComments.add(todoComment);
        todoComment.updateTodo(this);
    }

    public void removeTodoComment(TodoComment todoComment) {
        this.todoComments.remove(todoComment);
        todoComment.updateTodo(null);
    }

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateMember(Member member) {
        this.member = member;
    }
}
