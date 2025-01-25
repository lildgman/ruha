package com.ruha.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
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
}
