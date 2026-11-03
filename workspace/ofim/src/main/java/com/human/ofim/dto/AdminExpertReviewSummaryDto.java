package com.human.ofim.dto;

import lombok.Data;

@Data
public class AdminExpertReviewSummaryDto {

    // 전체 전문가 수
    private int totalCount;

    // 오늘 신청 전문가 수
    private int todayAppliedCount;

    // 오늘 승인된 전문가 수
    private int todayApprovedCount;

    // 승인 대기 수 (approve_status = 0)
    private int waitingCount;

    // 반려 수 (approve_status = 2)
    private int rejectedCount;

}