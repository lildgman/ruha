package com.ruha.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(indexes = {
    @Index(name = "idx_todo_member_id", columnList = "member_id"),
    @Index(name = "idx_todo_category_id", columnList = "category_id"),
    @Index(name = "idx_todo_completed", columnList = "isCompleted"),
    @Index(name = "idx_todo_public", columnList = "isPublic"),
    @Index(name = "idx_todo_created_at", columnList = "createdAt")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Todo extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
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

    @OneToMany(mappedBy = "todo", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<TodoImage> todoImages = new ArrayList<>();

    @OneToMany(mappedBy = "todo", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();


    @PrePersist
    public void prePersist() {
        this.isCompleted = false;
    }

    //== 연관관계 편의 메소드 ==//
    public void addTodoImage(TodoImage todoImage) {
        if (todoImage == null) return;
        
        this.todoImages.add(todoImage);
        if (todoImage.getTodo() != this) {
            todoImage.updateTodo(this);
        }
    }

    public void removeTodoImage(TodoImage todoImage) {
        if (todoImage == null) return;
        
        this.todoImages.remove(todoImage);
        if (todoImage.getTodo() == this) {
            todoImage.updateTodo(null);
        }
    }

    public void addComment(Comment comment) {
        if (comment == null) return;
        
        this.comments.add(comment);
        if (comment.getTodo() != this) {
            comment.updateTodo(this);
        }
    }

    public void removeComment(Comment comment) {
        if (comment == null) return;
        
        this.comments.remove(comment);
        if (comment.getTodo() == this) {
            comment.updateTodo(null);
            // Member 쪽 관계도 정리
            if (comment.getMember() != null) {
                comment.getMember().removeComment(comment);
            }
        }
    }

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateMember(Member member) {
        this.member = member;
    }

    public void updateCategory(Category category) {
        this.category = category;
    }
}
