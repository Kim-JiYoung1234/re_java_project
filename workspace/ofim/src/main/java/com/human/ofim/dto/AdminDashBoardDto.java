package com.human.ofim.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminDashBoardDto {

    // ===== 상단 카드 =====
    private long totalUserCount;
    private long totalPersonalCount;
    private long totalPartnerCount;
    private long totalExpertCount;
    private long totalAdminCount;

    private long todayJoinCount;

    // ===== 그래프(6개월) =====
    private String metric;
    private String memberScope;

    private List<String> labels;
    private List<Long> values;

    // ===== 고객문의 =====
    private long inquiryTotalCount;
    private long inquiryNewCount;
    private long inquiryUnreadCount;
    private long inquiryInProgressCount;

    // ===== 더미 매출 =====
    private long dummyAdRevenue;
    private long dummyPaidRevenue;

    // ===== 전문가 승인 =====
    private long pendingExpertCount;

    // =============================
    // ⭐ 추가할 필드
    // =============================

    // 게시판 글 수
    private long totalPostCount;

    private long freeCount;
    private long freeTodayCount;

    private long qnaCount;
    private long qnaTodayCount;

    private long cardnewsCount;
    private long cardnewsTodayCount;

    private long inquiryPostCount;
    private long inquiryTodayPostCount;

    // 방문자 그래프
    private List<String> visitorLabels;
    private List<Long> visitorValues;

    // 인기글 / 최신글
    private List<AdminBoardItemDto> popularContents;
    private List<AdminBoardItemDto> latestPosts;
}