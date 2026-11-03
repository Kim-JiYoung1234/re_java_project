package com.human.ofim.dto;

import lombok.Data;

@Data
public class AdvertisementDto {
    private int adId;
    private String adTitle;
    private String adContent;
    private String startDate;
    private String endDate;
    private int partnerId;
    private int centerNo;
    private String adType;         // TEXT_KEYWORD, IMAGE_BANNER 등
    private String targetKeyword;
    private String billingModel;   // CPT(정액제), CPC(종량제)
    private long remainingBudget;  // 남은 예산
    private int costPerAction;     // 1회 클릭 시 차감액
    private int adStatus;          // 0:대기, 1:진행중, 2:종료/만료
}