package com.ruha.repository;

import com.ruha.entity.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    // 닉네임 사용 여부 확인
    boolean existsByNickname(String nickname);

    Optional<Member> findByNickname(String nickname);

}
