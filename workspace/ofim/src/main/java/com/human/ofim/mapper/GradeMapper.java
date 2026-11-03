package com.human.ofim.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface GradeMapper {
	
	int updateMembership(@Param("memberId") long memberId,
						@Param("gradeName") String planName);

}