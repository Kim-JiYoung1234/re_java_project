package com.human.ofim.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SecurityController {

    @GetMapping("/security-warning")
    public String securityWarning(
            @RequestParam(name = "ip", required = false, defaultValue = "UNKNOWN") String ip,
            @RequestParam(name = "reason", required = false, defaultValue = "비정상적인 접근 시도") String reason,
            Model model) {
        
        // 🌟 로그 확인용 (콘솔에 찍히는지 보세요)
        System.out.println("⚠️ [경고 페이지 작동] 차단된 IP: " + ip + " / 사유: " + reason);
        
        model.addAttribute("ip", ip);
        model.addAttribute("reason", reason);
        
        return "security/warning"; 
    }
}