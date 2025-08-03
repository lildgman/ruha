package com.ruha.entity;

import com.ruha.exception.follow.DuplicateFollowException;
import com.ruha.exception.follow.SelfFollowNotAllowedException;
import com.ruha.exception.member.MemberNotFoundException;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(indexes = {
    @Index(name = "idx_member_nickname", columnList = "nickname"),
    @Index(name = "idx_member_created_at", columnList = "createdAt")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long memberId;

    @NotBlank
    @Size(min = 4, max = 15)
    @Column(unique = true, nullable = false, length = 15)
    private String nickname;

    @NotBlank
    @Column(nullable = false)
    private String password;

    @NotBlank
    @Size(min = 2, max = 12)
    @Column(nullable = false, length = 12)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private RoleType roleType = RoleType.NORMAL;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Todo> todos = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "follower", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Follow> followings = new ArrayList<>();

    @OneToMany(mappedBy = "following", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Follow> followers = new ArrayList<>();

    public void updateName(String newName) {
        this.name = newName;
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    public void delete() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }

    //== 연관관계 편의 메소드 ==//
    public void addTodo(Todo todo) {
        if (todo == null) return;
        
        this.todos.add(todo);
        if (todo.getMember() != this) {
            todo.updateMember(this);
        }
    }

    public void removeTodo(Todo todo) {
        if (todo == null) return;
        
        this.todos.remove(todo);
        if (todo.getMember() == this) {
            todo.updateMember(null);
        }
    }

    public void addComment(Comment comment) {
        if (comment == null) return;
        
        this.comments.add(comment);
        if (comment.getMember() != this) {
            comment.updateMember(this);
        }
    }

    public void removeComment(Comment comment) { 
        if (comment == null) return;
        
        this.comments.remove(comment);
        if (comment.getMember() == this) {
            comment.updateMember(null);
        }
    }



}

