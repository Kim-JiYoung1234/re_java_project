package com.human.ofim.dto;

import lombok.Data;

@Data
public class AdRequestDto {
    private Integer centerNo;       // 광고할 센터 번호
    private String adType;          // '상위노출형' or '배너형'
    private String targetKeyword;   // 타겟 키워드 (상위노출형일 때만)
    private String adTitle;         // 광고 제목
    private String adContent;       // 광고 내용
    private Integer durationDays;   // 광고 기간 (7일, 15일, 30일 등)
    private Long paymentAmount;     // 최종 결제 금액
}