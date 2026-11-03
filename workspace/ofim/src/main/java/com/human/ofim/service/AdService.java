package com.human.ofim.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.human.ofim.dto.AdRequestDto;
import com.human.ofim.dto.FitnessCenterDto;
import com.human.ofim.mapper.AdMapper;

@Service
public class AdService {

    @Autowired
    private AdMapper adMapper;

    public List<FitnessCenterDto> getPartnerCenters(Long partnerId) {
        return adMapper.getMyCenters(partnerId);
    }

    @Transactional
    public void processAdPayment(AdRequestDto dto, Long partnerId) {
        Map<String, Object> adParams = new HashMap<>();
        adParams.put("centerNo", dto.getCenterNo());
        adParams.put("adTitle", dto.getAdTitle());
        adParams.put("adContent", dto.getAdContent());
        adParams.put("durationDays", dto.getDurationDays());
        adParams.put("partnerId", partnerId);
        adParams.put("adType", dto.getAdType());
        adParams.put("targetKeyword", "상위노출형".equals(dto.getAdType()) ? dto.getTargetKeyword() : null);
        adParams.put("paymentAmount", dto.getPaymentAmount()); // 입찰가 추가

        adMapper.insertAdvertisement(adParams);

        Map<String, Object> payParams = new HashMap<>();
        payParams.put("partnerId", partnerId);
        payParams.put("paymentType", dto.getAdType());
        payParams.put("paymentAmount", dto.getPaymentAmount());
        payParams.put("packageName", dto.getDurationDays() + "일 " + dto.getAdType() + " 패키지(입찰)");
        
        adMapper.insertPartnerPayment(payParams);

        // [중요 수정] 등록 시점에는 updateCenterAdFlag를 호출하지 않습니다! (심사 대기 상태)
    }

    public List<FitnessCenterDto> getPartnerAdStatusList(Long partnerId) {
        return adMapper.selectPartnerAdStatusList(partnerId);
    }
    public List<Map<String, Object>> getRevenueList() { return adMapper.selectRevenueList(); }
    public Long getTotalRevenue() { return adMapper.selectTotalRevenue(); }

    // [신규] 관리자용 대기열 조회
    public List<Map<String, Object>> getPendingAds() {
        return adMapper.selectPendingAds();
    }

    // [신규] 관리자 승인 로직
    @Transactional
    public void approveAd(Integer adId, Integer centerNo) {
        adMapper.updateAdStatus(adId, 1); // 1: 승인
        adMapper.updateCenterAdFlag(centerNo, 1); // 실제 리스트에 광고 노출 켜기
    }

    // [신규] 관리자 반려 로직
    @Transactional
    public void rejectAd(Integer adId) {
        adMapper.updateAdStatus(adId, 2); // 2: 반려 (실무에선 환불 로직 추가됨)
    }
}