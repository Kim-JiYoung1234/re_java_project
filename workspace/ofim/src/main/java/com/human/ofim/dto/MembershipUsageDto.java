package com.human.ofim.dto;

import lombok.Data;

@Data
public class MembershipUsageDto {

    private Long memberId;
    private String usageMonth;
    private Integer gradeId;

    private Integer chatUsedCount;
    private Integer phoneUsedCount;
    private Integer chatPhoneUsedCount;
    private Integer inquiryUsedCount;
    private Integer carryoverChatPhoneCount;

    private Integer remainChatCount;
    private Integer remainPhoneCount;
    private Integer remainChatPhoneCount;
    private Integer remainInquiryCount;
}