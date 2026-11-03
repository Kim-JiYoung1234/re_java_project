package com.human.ofim.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.human.ofim.dto.MembershipStatusDto;
import com.human.ofim.dto.MembershipUsageDto;

@Mapper
public interface MembershipMapper {

	MembershipStatusDto selectMembershipStatus(@Param("memberId") Long memberId);

	MembershipUsageDto selectMembershipUsage(@Param("memberId") Long memberId, @Param("usageMonth") String usageMonth);

	int insertMonthlyUsageRow(@Param("memberId") Long memberId, @Param("usageMonth") String usageMonth,
			@Param("gradeId") Integer gradeId);

	// 채팅,문의 사용량 증감
	int increaseChatUsedCount(@Param("memberId") Long memberId, @Param("usageMonth") String usageMonth);

	int increaseChatPhoneUsedCount(@Param("memberId") Long memberId, @Param("usageMonth") String usageMonth);

	int increaseInquiryUsedCount(@Param("memberId") Long memberId, @Param("usageMonth") String usageMonth);
}