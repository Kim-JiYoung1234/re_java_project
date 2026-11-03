package com.human.ofim.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpSession;

@Component
public class SessionManager {
    // loginId를 키로, 세션을 값으로 저장하는 전역 지도
    private static final Map<String, HttpSession> sessionMap = new ConcurrentHashMap<>();

    public void registerSession(String loginId, HttpSession newSession) {
        // 1. 이미 동일한 아이디로 접속 중인 다른 세션이 있는지 확인
        HttpSession oldSession = sessionMap.get(loginId);
        
        if (oldSession != null && !oldSession.getId().equals(newSession.getId())) {
            try {
                // 2. 있다면 이전 사용자의 세션을 즉시 파괴 (중복 로그인 차단 핵심)
                oldSession.invalidate();
            } catch (Exception e) {
                // 이미 만료된 세션이면 무시
            }
        }
        
        // 3. 현재 접속한 사용자의 새로운 세션을 등록
        sessionMap.put(loginId, newSession);
    }
    
    // 로그아웃 시 세션 제거용 (선택 사항)
    public void removeSession(String loginId) {
        sessionMap.remove(loginId);
    }
}