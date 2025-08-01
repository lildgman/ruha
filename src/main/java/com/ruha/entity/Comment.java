package com.ruha.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(indexes = {
    @Index(name = "idx_comment_member_id", columnList = "member_id"),
    @Index(name = "idx_comment_todo_id", columnList = "todo_id"),
    @Index(name = "idx_comment_created_at", columnList = "createdAt")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Comment extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long commentId;

    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "todo_id", nullable = false)
    private Todo todo;

    public void updateContent(String content) {
        this.content = content;
    }

    public void updateTodo(Todo todo) {
        this.todo = todo;
    }

    public void updateMember(Member member) {
        this.member = member;
    }
}
