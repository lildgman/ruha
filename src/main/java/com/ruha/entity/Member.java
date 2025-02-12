package com.ruha.entity;

import com.ruha.dto.CreateMemberRequest;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    private String password;
    private String name;

    @Enumerated(EnumType.STRING)
    private RoleType roleType;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime created;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime updated;

    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Follow> followings = new ArrayList<>();

    @OneToMany(mappedBy = "following", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Follow> followers = new ArrayList<>();


    public static Member toEntity(CreateMemberRequest createMemberRequest) {
        return Member.builder()
                .email(createMemberRequest.getEmail())
                .password(createMemberRequest.getPassword())
                .name(createMemberRequest.getName())
                .build();
    }

    public void follow(Member member) {
        Follow follow = Follow.builder()
                .follower(this)
                .following(member)
                .build();
        this.getFollowings().add(follow);
        member.getFollowers().add(follow);
    }

    public void unfollow(Member member) {
        this.getFollowings().removeIf(follow -> follow.getFollowing().equals(member));
        member.getFollowers().removeIf(follow -> follow.getFollower().equals(this));
    }

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
}
