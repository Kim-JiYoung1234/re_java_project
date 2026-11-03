package com.human.ofim.dto;

import lombok.Data;

@Data
public class ConsultRequestDto {
    private int expertId;       // 전문가 PK
    private int memberId;       // 예약자(유저) PK
    private String consultType;  // 상담 유형 (채팅, 전화, 이메일)
    
    // 보완: 이메일 상담 시 필요한 제목 필드
    private String title;        
    
    private String reserveDate;  // yyyy-MM-dd
    private String reserveTime;  // HH:mm
    private String consultContent; // 상담 내용 또는 이메일 본문
}