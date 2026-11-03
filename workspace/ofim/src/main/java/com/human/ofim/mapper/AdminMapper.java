package com.human.ofim.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.human.ofim.dto.AdminBoardItemDto;

@Mapper
public interface AdminMapper {

    // 회원 타입별 카운트 + 오늘 가입자
    Map<String, Object> selectUserTypeCounts();

    // 최근 6개월 월별 가입자(0채움) - scope 필터 적용
    List<Map<String, Object>> selectMonthlySignup(@Param("memberScope") String memberScope);

    // 문의 요약(기존 게시판 재활용) -> 테이블/컬럼명에 맞춰 XML만 수정
    Map<String, Object> selectInquirySummary(@Param("inquiryCategoryId") int inquiryCategoryId);
    
    // 전문가 승인대기 수 
    long selectPendingExpertCount();
    
    Map<String,Object> selectBoardStats();

    List<AdminBoardItemDto> selectPopularContents();

    List<AdminBoardItemDto> selectLatestPosts();
}