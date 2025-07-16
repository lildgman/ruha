package com.ruha.entity;

import com.ruha.exception.DuplicateFollowException;
import com.ruha.exception.FollowErrorCode;
import com.ruha.exception.SelfFollowNotAllowedException;
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
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleType roleType;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime created;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime updated;

    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Follow> followings = new ArrayList<>();

    @OneToMany(mappedBy = "following", cascade = CascadeType.ALL, orphanRemoval = true , fetch = FetchType.LAZY)
    @Builder.Default
    private List<Follow> followers = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Todo> todos = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();

        this.created = now;
        this.updated = now;
        this.roleType = RoleType.NORMAL;
    }

    @PreUpdate
    public void preUpdate() {
        this.updated = LocalDateTime.now();
    }

    // 서비스 레이어로 옮겨야함
    // 비즈니스 코드
    public void follow(Member member) {

        if (this.getMemberId().equals(member.getMemberId())) {
            throw new SelfFollowNotAllowedException(FollowErrorCode.SELF_FOLLOW_NOT_ALLOWED);
        }

        boolean alreadyFollowing = this.followings.stream()
                .anyMatch(follow -> follow.getFollowing().getMemberId().equals(member.getMemberId()));

        if (alreadyFollowing) {
            throw new DuplicateFollowException(FollowErrorCode.DUPLICATE_FOLLOW);
        }

        Follow follow = Follow.builder()
                .follower(this)
                .following(member)
                .build();

        this.followings.add(follow);
        member.followers.add(follow);
    }

    public void unfollow(Member member) {
        this.followings.removeIf(follow -> follow.getFollowing().getMemberId().equals(member.getMemberId()));
        member.followers.removeIf(follow -> follow.getFollower().getMemberId().equals(this.memberId));
    }

    public void updateName(String newName) {
        this.name = newName;
    }

    //== 연관관계 편의 메소드 ==//
    public void addTodo(Todo todo) {
        this.todos.add(todo);
        todo.updateMember(this);
    }

}
