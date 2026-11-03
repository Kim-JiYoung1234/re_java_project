package com.human.ofim.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {
    private String type;       // 알림 타입 ("CONSULT", "SYSTEM")
    private Long receiverId;   // 알림을 받을 파트너의 memberId
    private String centerName; // 알림이 발생한 센터명
    private String message;    // 알림 내용
}