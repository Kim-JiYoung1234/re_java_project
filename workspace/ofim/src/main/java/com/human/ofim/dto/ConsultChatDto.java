package com.human.ofim.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class ConsultChatDto {

    private Integer consultId;
    private Long memberId;
    private Long expertId;

    private String consultType;   // 채팅, 전화 등
    private Integer status;

    private LocalDateTime consultStartDate;
    private LocalDateTime consultEndDate;
    private LocalDateTime createdDate;

}