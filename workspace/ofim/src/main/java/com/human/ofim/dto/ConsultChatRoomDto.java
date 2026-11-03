package com.human.ofim.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class ConsultChatRoomDto {

    private Integer consultId;
    private Long memberId;
    private Long expertId;
    private Integer status; // 1=예약신청, 2=예약확정, 3=상담완료, 4=취소
    
    private String memberNickname;   // 회원
    private String expertName;       // 전문가

    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private Integer unreadCount;
}