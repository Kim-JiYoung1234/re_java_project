package com.human.ofim.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class MembershipStatusDto {

    private Long memberId;
    private Integer gradeId;
    private String gradeName;

    private Integer monthlyPrice;

    private Integer monthlyChatLimit;
    private Integer monthlyPhoneLimit;
    private Integer monthlyChatPhoneLimit;
    private Integer monthlyInquiryLimit;

    private String dietReviewYn;
    private String diaryReportYn;
    private String premiumContentYn;
    private String fullVideoYn;
    private String carryoverYn;
    private String weeklyReportYn;

    private String subscriptionStatus;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime nextBillingDate;
    private String autoRenewYn;
}