package com.human.ofim.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.human.ofim.dto.AdminBoardItemDto;
import com.human.ofim.dto.AdminDashBoardDto;
import com.human.ofim.mapper.AdminMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminMapper adminMapper;

    @Override
    public AdminDashBoardDto getDashboardData() {

        AdminDashBoardDto dashboard = new AdminDashBoardDto();

        /*
         * 1️⃣ 회원 통계
         */
        Map<String,Object> userCounts = adminMapper.selectUserTypeCounts();

        dashboard.setTotalUserCount(((Number) userCounts.get("totalUserCount")).longValue());
        dashboard.setTotalPersonalCount(((Number) userCounts.get("totalPersonalCount")).longValue());
        dashboard.setTotalPartnerCount(((Number) userCounts.get("totalPartnerCount")).longValue());
        dashboard.setTotalExpertCount(((Number) userCounts.get("totalExpertCount")).longValue());
        dashboard.setTotalAdminCount(((Number) userCounts.get("totalAdminCount")).longValue());

        dashboard.setTodayJoinCount(((Number) userCounts.get("todayJoinCount")).longValue());


        /*
         * 2️⃣ 문의 요약
         */
        Map<String,Object> inquiry = adminMapper.selectInquirySummary(6);

        dashboard.setInquiryTotalCount(((Number) inquiry.get("inquiryTotalCount")).longValue());
        dashboard.setInquiryNewCount(((Number) inquiry.get("inquiryNewCount")).longValue());
        dashboard.setInquiryUnreadCount(((Number) inquiry.get("inquiryUnreadCount")).longValue());
        dashboard.setInquiryInProgressCount(((Number) inquiry.get("inquiryInProgressCount")).longValue());


        /*
         * 3️⃣ 게시판 통계
         */
        Map<String,Object> boardStats = adminMapper.selectBoardStats();

        dashboard.setTotalPostCount(((Number) boardStats.get("totalPostCount")).longValue());

        dashboard.setFreeCount(((Number) boardStats.get("freeCount")).longValue());
        dashboard.setFreeTodayCount(((Number) boardStats.get("freeTodayCount")).longValue());

        dashboard.setQnaCount(((Number) boardStats.get("qnaCount")).longValue());
        dashboard.setQnaTodayCount(((Number) boardStats.get("qnaTodayCount")).longValue());

        dashboard.setCardnewsCount(((Number) boardStats.get("cardnewsCount")).longValue());
        dashboard.setCardnewsTodayCount(((Number) boardStats.get("cardnewsTodayCount")).longValue());

        dashboard.setInquiryPostCount(((Number) boardStats.get("inquiryPostCount")).longValue());
        dashboard.setInquiryTodayPostCount(((Number) boardStats.get("inquiryTodayPostCount")).longValue());


        /*
         * 4️⃣ 인기 콘텐츠
         */
        List<AdminBoardItemDto> popular = adminMapper.selectPopularContents();
        dashboard.setPopularContents(popular);


        /*
         * 5️⃣ 최신 글
         */
        List<AdminBoardItemDto> latest = adminMapper.selectLatestPosts();
        dashboard.setLatestPosts(latest);


        /*
         * 6️⃣ 전문가 승인 대기
         */
        dashboard.setPendingExpertCount(adminMapper.selectPendingExpertCount());


        /*
         * 7️⃣ 방문자 더미 (나중에 방문로그 붙이면 교체)
         */
        dashboard.setVisitorLabels(
                List.of("3/4","3/5","3/6","3/7","3/8","3/9","3/10")
        );

        dashboard.setVisitorValues(
                List.of(52L,61L,70L,65L,80L,90L,128L)
        );

        return dashboard;
    }
}