package com.human.ofim.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.human.ofim.dto.MemberDto;

@Mapper
public interface MemberTypeMapper {

	MemberDto selectMemberTypeById(long memberId);
}
