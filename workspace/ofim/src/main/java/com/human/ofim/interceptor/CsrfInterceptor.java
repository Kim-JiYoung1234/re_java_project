package com.human.ofim.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.UUID;
import com.human.ofim.service.SecurityService; // 🌟 추가

@Component
public class CsrfInterceptor implements HandlerInterceptor {

    @Autowired
    private SecurityService securityService; // 🌟 추가

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        
        String clientIp = getClientIp(request);

        // 🌟 1. 아예 3회 이상 걸린 놈은 아무것도 못하게 입구컷
        if (securityService.isBanned(clientIp)) {
            response.sendRedirect("/security-warning?ip=" + clientIp + "&reason=BANNED");
            return false;
        }

        HttpSession session = request.getSession();
        
        if (session.getAttribute("csrfToken") == null) {
            session.setAttribute("csrfToken", UUID.randomUUID().toString());
        }

        String method = request.getMethod();
        if (!"POST".equalsIgnoreCase(method) && !"PUT".equalsIgnoreCase(method) && !"DELETE".equalsIgnoreCase(method)) {
            return true;
        }

        String sessionToken = (String) session.getAttribute("csrfToken");
        String requestToken = request.getParameter("csrfToken");

        if (sessionToken == null || !sessionToken.equals(requestToken)) {
            // 🌟 2. CSRF 위조 적발! -> DB에 기록하고 관리자에게 알림
            String reason = "CSRF 토큰 위조 시도";
            securityService.handleSecurityViolation(clientIp, reason);
            
            // 🌟 3. 공포의 박제 페이지로 강제 이동
            response.sendRedirect("/security-warning?ip=" + clientIp + "&reason=CSRF_ATTACK");
            return false;
        }

        return true;
    }

    // IP 추출 로직 (AdminInterceptor에서 복사해옴)
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) ip = request.getHeader("Proxy-Client-IP");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) ip = request.getRemoteAddr();
        return ip;
    }
}