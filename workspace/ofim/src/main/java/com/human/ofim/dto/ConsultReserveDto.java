package com.human.ofim.dto;

import java.time.LocalDateTime;

import lombok.Data;

public class ConsultReserveDto {
	private Long memberId;     // 로그인한 사용자
    private Long expertId;     // 예약 대상 전문가
    private Integer consultType; // tinyint (예: 1=채팅, 2=전화 등)
    private LocalDateTime reserveDateTime; // 시작시간 (YYYY-MM-DDTHH:mm)
}