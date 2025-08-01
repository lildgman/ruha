package com.ruha.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
    uniqueConstraints = {
        @UniqueConstraint(name = "UniqueFollowerAndFollowing", columnNames = {"follower_id", "following_id"})
    },
    indexes = {
        @Index(name = "idx_follow_follower_id", columnList = "follower_id"),
        @Index(name = "idx_follow_following_id", columnList = "following_id"),
        @Index(name = "idx_follow_created_at", columnList = "createdAt")
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Follow extends BaseCreatedAtEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long followId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id")
    private Member follower;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "following_id")
    private Member following;

}
