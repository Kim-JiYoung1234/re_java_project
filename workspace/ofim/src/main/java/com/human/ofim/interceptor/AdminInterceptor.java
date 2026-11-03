package com.human.ofim.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import com.human.ofim.service.SecurityService; // 🌟 추가

@Component
public class AdminInterceptor implements HandlerInterceptor {

    @Value("${admin.allowed-ips:127.0.0.1,0:0:0:0:0:0:0:1}")
    private String allowedIpsString;

    @Autowired
    private SecurityService securityService; // 🌟 추가

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String clientIp = getClientIp(request);
        
        // 🌟 1. 블랙리스트 IP면 바로 차단
        if (securityService.isBanned(clientIp)) {
            response.sendRedirect("/security-warning?ip=" + clientIp + "&reason=BANNED");
            return false;
        }

        List<String> allowedIps = Arrays.stream(allowedIpsString.split(","))
                                        .map(String::trim)
                                        .collect(Collectors.toList());
        
        if (!allowedIpsString.equals("*") && !allowedIps.contains(clientIp)) {
            // 🌟 2. 허가되지 않은 IP의 관리자 접근 시도! -> DB 기록 및 알림
            securityService.handleSecurityViolation(clientIp, "관리자 페이지 무단 접근");
            
            // 🌟 3. 공포의 박제 페이지로 이동
            response.sendRedirect("/security-warning?ip=" + clientIp + "&reason=UNAUTHORIZED_ADMIN_ACCESS");
            return false;
        }

        HttpSession session = request.getSession();
        if (session.getAttribute("adminUser") == null) {
            response.sendRedirect("/admin/login");
            return false;
        }
        return true; 
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) ip = request.getHeader("Proxy-Client-IP");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) ip = request.getRemoteAddr();
        return ip;
    }
}