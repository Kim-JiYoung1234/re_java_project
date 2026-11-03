package com.human.ofim.service;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.human.ofim.dto.MembershipStatusDto;
import com.human.ofim.dto.MembershipUsageDto;
import com.human.ofim.mapper.MembershipMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MembershipServiceImpl implements MembershipService {

    private final MembershipMapper membershipMapper;

    private String getCurrentUsageMonth() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
    }

    @Override
    public MembershipStatusDto getMembershipStatus(Long memberId) {
        return membershipMapper.selectMembershipStatus(memberId);
    }

    @Override
    @Transactional
    public void ensureMonthlyUsageRow(Long memberId) {
        String usageMonth = getCurrentUsageMonth();

        MembershipUsageDto usage = membershipMapper.selectMembershipUsage(memberId, usageMonth);
        if (usage != null) {
            return;
        }

        MembershipStatusDto status = membershipMapper.selectMembershipStatus(memberId);
        if (status == null) {
            throw new IllegalArgumentException("회원 등급 정보를 찾을 수 없습니다.");
        }

        membershipMapper.insertMonthlyUsageRow(memberId, usageMonth, status.getGradeId());
    }

    @Override
    @Transactional
    public MembershipUsageDto getMembershipUsage(Long memberId) {
        String usageMonth = getCurrentUsageMonth();

        ensureMonthlyUsageRow(memberId);

        return membershipMapper.selectMembershipUsage(memberId, usageMonth);
    }

    @Override
    @Transactional
    public void validateChatConsultAvailable(Long memberId) {
        MembershipStatusDto status = getMembershipStatus(memberId);
        if (status == null) {
            throw new IllegalArgumentException("회원 등급 정보를 찾을 수 없습니다.");
        }

        MembershipUsageDto usage = getMembershipUsage(memberId);
        if (usage == null) {
            throw new IllegalArgumentException("사용량 정보를 찾을 수 없습니다.");
        }

        Integer gradeId = status.getGradeId();

        // 1: 일반회원 / 2: 브론즈 / 3: 실버 / 4: 골드 / 5: 프리미엄
        if (gradeId == 1 || gradeId == 2) {
            throw new IllegalArgumentException("현재 등급에서는 채팅 상담을 이용할 수 없습니다.");
        }

        if (gradeId == 3) {
            if (usage.getRemainChatCount() == null || usage.getRemainChatCount() <= 0) {
                throw new IllegalArgumentException("이번 달 채팅 상담 가능 횟수를 모두 사용했습니다.");
            }
            return;
        }

        if (gradeId == 4 || gradeId == 5) {
            if (usage.getRemainChatPhoneCount() == null || usage.getRemainChatPhoneCount() <= 0) {
                throw new IllegalArgumentException("이번 달 채팅/전화 상담 가능 횟수를 모두 사용했습니다.");
            }
            return;
        }

        throw new IllegalArgumentException("이용할 수 없는 회원 등급입니다.");
    }

    @Override
    @Transactional
    public void useChatConsult(Long memberId) {
        MembershipStatusDto status = getMembershipStatus(memberId);
        if (status == null) {
            throw new IllegalArgumentException("회원 등급 정보를 찾을 수 없습니다.");
        }

        validateChatConsultAvailable(memberId);

        String usageMonth = getCurrentUsageMonth();
        Integer gradeId = status.getGradeId();

        int updated;
        if (gradeId == 3) {
            updated = membershipMapper.increaseChatUsedCount(memberId, usageMonth);
        } else if (gradeId == 4 || gradeId == 5) {
            updated = membershipMapper.increaseChatPhoneUsedCount(memberId, usageMonth);
        } else {
            throw new IllegalArgumentException("채팅 상담 차감 대상이 아닌 등급입니다.");
        }

        if (updated == 0) {
            throw new IllegalArgumentException("상담 사용량 차감에 실패했습니다.");
        }
    }
    
    @Override
    @Transactional
    public void validateInquiryAvailable(Long memberId) {
        MembershipStatusDto status = getMembershipStatus(memberId);
        if (status == null) {
            throw new IllegalArgumentException("회원 등급 정보를 찾을 수 없습니다.");
        }

        MembershipUsageDto usage = getMembershipUsage(memberId);
        if (usage == null) {
            throw new IllegalArgumentException("사용량 정보를 찾을 수 없습니다.");
        }

        Integer remain = usage.getRemainInquiryCount();
        if (remain == null || remain <= 0) {
            throw new IllegalArgumentException("이번 달 1:1 문의 가능 횟수를 모두 사용했습니다.");
        }
    }

    @Override
    @Transactional
    public void useInquiry(Long memberId) {
        validateInquiryAvailable(memberId);

        String usageMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        int updated = membershipMapper.increaseInquiryUsedCount(memberId, usageMonth);

        if (updated == 0) {
            throw new IllegalArgumentException("문의 사용량 차감에 실패했습니다.");
        }
    }
}