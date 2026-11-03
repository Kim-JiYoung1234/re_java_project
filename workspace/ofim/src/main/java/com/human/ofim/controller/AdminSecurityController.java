package com.human.ofim.controller;

import com.human.ofim.mapper.MemberMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/security")
public class AdminSecurityController extends AdminBaseController {

    @Autowired
    private MemberMapper memberMapper;

    @GetMapping
    public String securityDashboard(Model model) {
        // 1. 실시간 감사 로그 최근 20개 가져오기
        List<Map<String, Object>> auditLogs = memberMapper.selectRecentLoginAudit();
        
        // 2. 오늘의 통계 데이터 가져오기 (성공/실패/차단 수)
        Map<String, Object> stats = memberMapper.selectTodayLoginStats();

        model.addAttribute("auditLogs", auditLogs);
        model.addAttribute("stats", stats);

        return render(model, "admin/layout", "보안 및 감사 로그", "실시간 로그인 시도 및 차단 내역 모니터링", "security");
    }
}