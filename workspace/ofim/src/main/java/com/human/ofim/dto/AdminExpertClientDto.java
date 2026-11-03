package com.human.ofim.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class AdminExpertClientDto {
    private Long memberId;
    private String clientLoginId;
    private String clientName;
    private String clientEmail;

    private Long consultCount;
    private Long totalMinutes;
    private LocalDateTime lastConsultAt; // 보기용(yyyy-MM-dd HH:mm)
}