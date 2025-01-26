package com.ruha.entity;

import com.ruha.dto.CreateFollowRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Follow {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id")
    private Member follower;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "following_id")
    private Member following;

    @Column(nullable = false)
    private LocalDateTime followedAt;

    @PrePersist
    public void prePersist() {
        this.followedAt = LocalDateTime.now();
    }

    public static Follow toEntity(CreateFollowRequest request) {
        return Follow.builder()
                .follower(request.getFollower())
                .following(request.getFollowing())
                .build();
    }
}
