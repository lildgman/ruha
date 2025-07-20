package com.ruha.dto.member;

import com.ruha.entity.Member;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberResponse {

    private final Long memberId;
    private final String nickname;
    private final String name;
    private final LocalDateTime createdAt;

    public static MemberResponse from(Member member) {

        return new MemberResponse(
                member.getMemberId(),
                member.getNickname(),
                member.getName(),
                member.getCreatedAt()
        );
    }
}
