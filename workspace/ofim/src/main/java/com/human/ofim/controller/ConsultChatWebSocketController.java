package com.human.ofim.controller;

import java.util.Map;

import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.human.ofim.dto.ConsultChatMessageDto;
import com.human.ofim.service.ConsultChatService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ConsultChatWebSocketController {

    private final ConsultChatService consultChatService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/consult/chat/send")
    public void sendMessage(ConsultChatMessageDto message,
                            @Header("simpSessionAttributes") Map<String, Object> sessionAttributes) {

        Object loginMemberIdObj = sessionAttributes.get("loginMemberId");
        if (loginMemberIdObj == null) {
            throw new RuntimeException("로그인 정보가 없습니다.");
        }

        Long loginMemberId;
        if (loginMemberIdObj instanceof Long) {
            loginMemberId = (Long) loginMemberIdObj;
        } else if (loginMemberIdObj instanceof Integer) {
            loginMemberId = ((Integer) loginMemberIdObj).longValue();
        } else {
            loginMemberId = Long.valueOf(loginMemberIdObj.toString());
        }

        ConsultChatMessageDto saved = consultChatService.saveMessage(message, loginMemberId);

        messagingTemplate.convertAndSend(
                "/topic/consult/" + saved.getConsultId(),
                saved
        );
    }
}