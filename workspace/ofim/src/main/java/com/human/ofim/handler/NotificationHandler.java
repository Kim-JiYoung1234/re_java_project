package com.human.ofim.handler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.human.ofim.dto.NotificationDto; // ◀ 이 부분이 핵심입니다!

@Component
public class NotificationHandler extends TextWebSocketHandler {

    private final Map<String, WebSocketSession> userSessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        
        if (payload.startsWith("LOGIN:")) {
            String memberId = payload.substring(6);
            userSessions.put(memberId, session);
            return;
        }

        try {
            // 이제 NotificationDto를 정확히 인식하므로 readValue 에러도 사라집니다.
            NotificationDto notification = objectMapper.readValue(payload, NotificationDto.class);
            String receiverIdStr = String.valueOf(notification.getReceiverId());
            
            WebSocketSession receiverSession = userSessions.get(receiverIdStr);
            if (receiverSession != null && receiverSession.isOpen()) {
                receiverSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(notification)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        userSessions.values().removeIf(s -> s.getId().equals(session.getId()));
    }
}