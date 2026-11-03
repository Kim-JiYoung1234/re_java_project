package com.human.ofim.mapper;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.human.ofim.dto.FitnessCenterDto;
import com.human.ofim.dto.AdvertisementDto;

@Mapper
public interface AdMapper {
    List<FitnessCenterDto> getMyCenters(@Param("partnerId") Long partnerId);
    void insertAdvertisement(Map<String, Object> params);
    void insertPartnerPayment(Map<String, Object> params);
    void updateCenterAdFlag(@Param("centerNo") Integer centerNo, @Param("isAd") int isAd);
    List<FitnessCenterDto> selectPartnerAdStatusList(@Param("partnerId") Long partnerId);
    List<Map<String, Object>> selectRevenueList();
    Long selectTotalRevenue();
    
    Map<String, Object> selectAdSummary(@Param("partnerId") Long partnerId);
    List<Map<String, Object>> selectDailyClickCounts(@Param("partnerId") Long partnerId);
    List<Map<String, Object>> selectKeywordDistribution(@Param("partnerId") Long partnerId);

    // [신규 추가] 관리자 심사 대기열 (입찰가 높은 순)
    List<Map<String, Object>> selectPendingAds();
    
    // [신규 추가] 광고 상태(승인/반려) 업데이트
    void updateAdStatus(@Param("adId") Integer adId, @Param("status") int status);

    // =======================================================
    // [종량제 신규 추가] 실시간 차감 및 로그 기록용 메서드
    // =======================================================
    AdvertisementDto findActiveAdByCenterNo(@Param("centerNo") int centerNo);
    
    int deductAdBudget(@Param("adId") int adId);
    
    int insertAdClickLog(@Param("adId") int adId, @Param("centerNo") int centerNo);

    // [해결] 이 부분이 빠져있어서 에러가 발생했습니다! (노출수 증가 메서드)
    int insertAdImpressionLog(@Param("adId") int adId, @Param("centerNo") int centerNo);
}