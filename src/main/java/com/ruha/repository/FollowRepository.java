package com.ruha.repository;

import com.ruha.entity.Follow;
import com.ruha.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    // 두 회원 간 팔로우 관계 조회
    Optional<Follow> findByFollowerAndFollowing(Member follower, Member following);

    // 두 회원 간 팔로우 관계 여부 확인
    boolean existsByFollowerAndFollowing(Member follower, Member following);

    // 특정 회원의 팔로잉 목록 조회
    List<Follow> findByFollower(Member follower);

    // 특정 회원의 팔로워 목록 조회
    List<Follow> findByFollowing(Member following);

    long countByFollower(Member member);

    long countByFollowing(Member member);


}
