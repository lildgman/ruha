package com.ruha.repository;

import com.ruha.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    // 닉네임 사용 여부 확인
    boolean existsByNickname(String nickname);

    Optional<Member> findByNickname(String nickname);

    // 활성 회원 조회 (탈퇴하지 않은 회원만)
    boolean existsByNicknameAndIsDeletedFalse(String nickname);

    Optional<Member> findByNicknameAndIsDeletedFalse(String nickname);

    Optional<Member> findByMemberIdAndIsDeletedFalse(Long memberId);

}
