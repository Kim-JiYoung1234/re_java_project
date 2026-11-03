package com.human.ofim.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.human.ofim.dto.CenterTicketDto;
import com.human.ofim.dto.FitnessCenterDto;
import com.human.ofim.dto.ReviewDto;

@Mapper
public interface FitnessMapper {
    void insertFitnessCenter(FitnessCenterDto dto);
    void insertWorkingHours(FitnessCenterDto dto);
    
    void insertCenterTickets(List<CenterTicketDto> tickets);

    // 🌟 [결제 시스템 2단계] 시설 번호로 티켓 목록 조회 쿼리
    List<CenterTicketDto> selectTicketsByCenterNo(Integer centerNo);

    List<FitnessCenterDto> findAll(@Param("keyword") String keyword, @Param("type") String type, @Param("dayPassYn") Boolean dayPassYn, @Param("is24h") Boolean is24h);
    FitnessCenterDto findById(Integer centerNo);

    int checkRecommend(@Param("centerNo") Integer centerNo, @Param("memberId") Long memberId);
    void insertRecommend(@Param("centerNo") Integer centerNo, @Param("memberId") Long memberId);
    void deleteRecommend(@Param("centerNo") Integer centerNo, @Param("memberId") Long memberId);
    int getRecommendCount(Integer centerNo);

    int checkBookmark(@Param("centerNo") Integer centerNo, @Param("memberId") Long memberId);
    void insertBookmark(@Param("centerNo") Integer centerNo, @Param("memberId") Long memberId);
    void deleteBookmark(@Param("centerNo") Integer centerNo, @Param("memberId") Long memberId);

    void insertReview(ReviewDto review);
    List<ReviewDto> selectBestReviews(Integer centerNo);
    void updateCenterReviewStats(Integer centerNo);

    List<FitnessCenterDto> selectPremiumBanners();

    List<ReviewDto> selectMyReviewsPaging(@Param("memberId") Long memberId, @Param("keyword") String keyword, @Param("startRow") int startRow, @Param("size") int size);
    int countMyReviewsPaging(@Param("memberId") Long memberId, @Param("keyword") String keyword);
    int deleteReview(@Param("reviewId") int reviewId, @Param("memberId") Long memberId);

    List<Map<String, Object>> selectMyBookmarkedCenters(@Param("memberId") Long memberId, @Param("keyword") String keyword, @Param("startRow") int startRow, @Param("size") int size);
    int countMyBookmarkedCenters(@Param("memberId") Long memberId, @Param("keyword") String keyword);

    List<ReviewDto> selectReviewsByPartnerPaging(@Param("partnerId") Long partnerId, @Param("startRow") int startRow, @Param("size") int size);
    int countReviewsByPartner(Long partnerId);
    int updateReviewReply(@Param("reviewId") int reviewId, @Param("replyContent") String replyContent);

    List<ReviewDto> selectAllReviewsByCenterNo(Integer centerNo);
}