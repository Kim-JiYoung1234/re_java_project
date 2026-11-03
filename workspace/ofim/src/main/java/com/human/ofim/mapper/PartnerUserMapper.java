package com.human.ofim.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.human.ofim.dto.PartnerUserDto;

@Mapper
public interface PartnerUserMapper {
	void insertPartner(PartnerUserDto partnerUserDto);
	
	// 파트너사의 승인 상태(0:대기, 1:승인)를 조회하기 위한 메서드 추가
	Integer selectApproveStatus(Long memberId);
}