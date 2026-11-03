package com.human.ofim.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import com.human.ofim.mapper.SecurityMapper;

@Service
public class SecurityService {

    @Autowired
    private SecurityMapper securityMapper;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // 🚨 위반자 처리 및 알림 발송 메서드
    public void handleSecurityViolation(String ip, String reason) {
        Integer count = securityMapper.getViolationCount(ip);

        if (count == null) {
            securityMapper.insertBlacklist(ip, reason); // 초범
            count = 1;
        } else {
            securityMapper.updateBlacklist(ip, reason); // 재범
            count++;
        }

        // 🌟 관리자 페이지로 실시간 웹소켓 알림 쏘기!
        String alertMessage = String.format("🚨 [보안경보] IP: %s | 사유: %s | 누적: %d회", ip, reason, count);
        messagingTemplate.convertAndSend("/topic/admin/alerts", alertMessage);
        
        System.out.println("🔥 [보안 서비스 작동] " + alertMessage);
    }
    
    // 이 IP가 영구 차단 대상인지 확인 (3회 이상 걸리면 아예 접속 불가)
    public boolean isBanned(String ip) {
        Integer count = securityMapper.getViolationCount(ip);
        return count != null && count >= 3; 
    }
}