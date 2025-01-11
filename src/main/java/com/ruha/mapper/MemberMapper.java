package com.ruha.mapper;

import com.ruha.entity.Member;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MemberMapper {

    void insertMember(Member member);
    List<Member> findAll();
}
