package com.human.ofim.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class ConsultChatMessageDto {

    private Long chatId;
    private Integer consultId;
    private Long senderId;
    
    private String senderNickname;  // 회원
    private String senderName;     // 전문가
    
    private String messageType;      // TALK, ENTER, LEAVE, SYSTEM
    private String messageContent;
    private LocalDateTime createdDate;
    private Integer isRead;

    private boolean mine;
    
 // ⭐ 화면용 (가공 필드)
    private String displayName;
}