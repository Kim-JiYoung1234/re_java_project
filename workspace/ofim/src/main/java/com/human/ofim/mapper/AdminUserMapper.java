package com.human.ofim.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.human.ofim.dto.MemberDto;


@Mapper
public interface AdminUserMapper {

    List<MemberDto> selectUserList(
            @Param("type") String type,
            @Param("grade") String grade,
            @Param("status") String status,
            @Param("keyword") String keyword,
            @Param("offset") int offset,
            @Param("size") int size
    );

    int countUsers(
            @Param("type") String type,
            @Param("grade") String grade,
            @Param("status") String status,
            @Param("keyword") String keyword
    );

    MemberDto selectUserDetail(@Param("memberId") Long memberId);

    int updateMemberStatus(
            @Param("memberId") int memberId,
            @Param("memberStatus") String memberStatus,
            @Param("reason") String reason
    );
}