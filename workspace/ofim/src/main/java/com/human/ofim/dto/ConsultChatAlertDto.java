package com.human.ofim.dto;

import lombok.Data;

@Data
public class ConsultChatAlertDto {
    private Integer consultId;
    private Long memberId;
    private String memberNickname;
    private Long expertId;
    private String expertName;
    private String lastMessage;
    private String createdTime;
    private boolean newChat;
}