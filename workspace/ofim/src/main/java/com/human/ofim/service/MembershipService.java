package com.human.ofim.service;

import com.human.ofim.dto.MembershipStatusDto;
import com.human.ofim.dto.MembershipUsageDto;

public interface MembershipService {

	MembershipStatusDto getMembershipStatus(Long memberId);

	MembershipUsageDto getMembershipUsage(Long memberId);

	void ensureMonthlyUsageRow(Long memberId);

	void validateChatConsultAvailable(Long memberId);

	void useChatConsult(Long memberId);

	// 상담 횟수
	void validateInquiryAvailable(Long memberId);

	void useInquiry(Long memberId);

}