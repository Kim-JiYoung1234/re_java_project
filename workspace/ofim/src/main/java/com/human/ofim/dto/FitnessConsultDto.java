package com.human.ofim.dto;

import lombok.Data;

@Data
public class FitnessConsultDto {
    private int consultNo;
    private int centerNo;
    private int partnerId;
    private String customerName;
    private String customerPhone;
    private String memo;
    private String regDate;
    private int status;
    private String centerName;
    
    // [신규] 관리자(파트너)가 남기는 상담 메모
    private String partnerMemo;
}