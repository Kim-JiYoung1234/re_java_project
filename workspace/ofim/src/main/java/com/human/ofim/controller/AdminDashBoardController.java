package com.human.ofim.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.human.ofim.dto.AdminBoardItemDto;
import com.human.ofim.dto.AdminDashBoardDto;
import com.human.ofim.dto.VisitorSummaryDto;
import com.human.ofim.mapper.AdminMapper;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminDashBoardController extends AdminBaseController {

    private final AdminMapper adminMapper;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        AdminDashBoardDto dashboard = new AdminDashBoardDto();

        // 1. 회원 통계
        Map<String, Object> userCounts = adminMapper.selectUserTypeCounts();
        dashboard.setTotalUserCount(getLong(userCounts, "totalUserCount"));
        dashboard.setTotalPersonalCount(getLong(userCounts, "totalPersonalCount"));
        dashboard.setTotalPartnerCount(getLong(userCounts, "totalPartnerCount"));
        dashboard.setTotalExpertCount(getLong(userCounts, "totalExpertCount"));
        dashboard.setTotalAdminCount(getLong(userCounts, "totalAdminCount"));
        dashboard.setTodayJoinCount(getLong(userCounts, "todayJoinCount"));

        // 2. 고객문의 요약 (categoryId 5 = 관리자에게)
        Map<String, Object> inquiry = adminMapper.selectInquirySummary(5);
        dashboard.setInquiryTotalCount(getLong(inquiry, "inquiryTotalCount"));
        dashboard.setInquiryNewCount(getLong(inquiry, "inquiryNewCount"));
        dashboard.setInquiryUnreadCount(getLong(inquiry, "inquiryUnreadCount"));
        dashboard.setInquiryInProgressCount(getLong(inquiry, "inquiryInProgressCount"));

        // 3. 게시판 통계
        Map<String, Object> boardStats = adminMapper.selectBoardStats();
        dashboard.setTotalPostCount(getLong(boardStats, "totalPostCount"));
        dashboard.setFreeCount(getLong(boardStats, "freeCount"));
        dashboard.setFreeTodayCount(getLong(boardStats, "freeTodayCount"));
        dashboard.setQnaCount(getLong(boardStats, "qnaCount"));
        dashboard.setQnaTodayCount(getLong(boardStats, "qnaTodayCount"));
        dashboard.setCardnewsCount(getLong(boardStats, "cardnewsCount"));
        dashboard.setCardnewsTodayCount(getLong(boardStats, "cardnewsTodayCount"));
        dashboard.setInquiryPostCount(getLong(boardStats, "inquiryPostCount"));
        dashboard.setInquiryTodayPostCount(getLong(boardStats, "inquiryTodayPostCount"));

        // 4. 인기글 / 최신글
        List<AdminBoardItemDto> popular = adminMapper.selectPopularContents();
        dashboard.setPopularContents(popular);

        List<AdminBoardItemDto> latest = adminMapper.selectLatestPosts();
        dashboard.setLatestPosts(latest);

        // 5. 전문가 승인 대기
        dashboard.setPendingExpertCount(adminMapper.selectPendingExpertCount());

        // 6. 방문자 더미 데이터
        List<String> labels = new ArrayList<>();
        List<Long> values = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d");

        for (int i = 6; i >= 0; i--) {
            LocalDate d = LocalDate.now().minusDays(i);
            labels.add(d.format(formatter));
            values.add((long) (50 + new Random().nextInt(100)));
        }

        dashboard.setVisitorLabels(labels);
        dashboard.setVisitorValues(values);

        // 7. 방문자 요약
        VisitorSummaryDto visitorSummary = new VisitorSummaryDto();
        visitorSummary.setTodayVisitors(values.get(values.size() - 1));
        visitorSummary.setWeekAvgVisitors(
                values.stream().mapToLong(Long::longValue).sum() / values.size()
        );

        model.addAttribute("dashboard", dashboard);
        model.addAttribute("visitorSummary", visitorSummary);

        // 챌린지는 아직 미연결
        model.addAttribute("challengeSummary", null);

        model.addAttribute("activeMenu", "dashboard");
        model.addAttribute("pageTitle", "대시보드");
        model.addAttribute("pageDesc", "관리 현황 요약");

        return "admin/dashboard";
    }

    @GetMapping("/api/dashboard/chart")
    @ResponseBody
    public Map<String, Object> getDashboardChart(
            @RequestParam(name = "metric", defaultValue = "SIGNUP") String metric,
            @RequestParam(name = "scope", defaultValue = "ALL") String scope) {

        Map<String, Object> result = new HashMap<>();

        if ("SIGNUP".equals(metric)) {
            List<Map<String, Object>> rows = adminMapper.selectMonthlySignup(scope);

            List<String> labels = new ArrayList<>();
            List<Long> values = new ArrayList<>();

            for (Map<String, Object> row : rows) {
                labels.add(String.valueOf(row.get("label")));
                Object valueObj = row.get("value");
                values.add(valueObj == null ? 0L : ((Number) valueObj).longValue());
            }

            result.put("labels", labels);
            result.put("values", values);
            return result;
        }

        // 광고매출 / 유료매출 더미
        List<String> labels = new ArrayList<>();
        List<Long> values = new ArrayList<>();

        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

        for (int i = 5; i >= 0; i--) {
            labels.add(now.minusMonths(i).format(formatter));
            values.add(0L);
        }

        result.put("labels", labels);
        result.put("values", values);
        return result;
    }

    private long getLong(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value == null ? 0L : ((Number) value).longValue();
    }
}