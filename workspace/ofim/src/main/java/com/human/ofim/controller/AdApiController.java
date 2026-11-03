package com.human.ofim.controller;

import com.human.ofim.dto.AdvertisementDto;
import com.human.ofim.dto.MemberDto; 
import com.human.ofim.mapper.AdMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/ad")
public class AdApiController {

    @Autowired
    private AdMapper adMapper; 

    // [해결 부분] @PathVariable 뒤에 ("centerNo") 를 명시적으로 추가했습니다!!
    @PostMapping("/click/{centerNo}")
    public ResponseEntity<String> recordAdClick(@PathVariable("centerNo") int centerNo) {
        
        AdvertisementDto activeAd = adMapper.findActiveAdByCenterNo(centerNo);
        
        if (activeAd != null) {
            // 팀원 DB 구조에 맞춰 'CPC' 또는 '클릭형'일 때 차감
            if ("CPC".equals(activeAd.getBillingModel()) || "클릭형".equals(activeAd.getAdType())) {
                adMapper.deductAdBudget(activeAd.getAdId()); 
                System.out.println("💳 [종량제 차감] 광고번호: " + activeAd.getAdId() + " 차감 완료!");
            }
            
            // [DB 에러 근본 원인 해결] DB에 없는 deducted_amount 파라미터 전송 제거
            adMapper.insertAdClickLog(activeAd.getAdId(), centerNo);
            //System.out.println("✅ [클릭 기록] 센터번호: " + centerNo + " 클릭 반영 완료!");
        } else {
            //System.out.println("⚠️ [광고 없음] 센터번호: " + centerNo + "의 활성화된 광고를 찾을 수 없습니다.");
        }
        
        return ResponseEntity.ok("success");
    }
    
    /**
     * [신규 추가] 화면에 광고가 노출되었을 때 노출수(Impression) 증가 API
     */
    @PostMapping("/impression/{centerNo}")
    public ResponseEntity<String> recordAdImpression(@PathVariable("centerNo") int centerNo) {
        AdvertisementDto activeAd = adMapper.findActiveAdByCenterNo(centerNo);
        if (activeAd != null) {
            adMapper.insertAdImpressionLog(activeAd.getAdId(), centerNo);
            //System.out.println("👁️ [노출 기록] 센터번호: " + centerNo + " 노출수 1 증가!");
        }
        return ResponseEntity.ok("success");
    }
    
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getAdStats(HttpSession session) {
        MemberDto user = (MemberDto) session.getAttribute("user");
        
        if (user == null) {
            return ResponseEntity.status(401).build(); 
        }
        
        Long partnerId = (long) user.getMemberId(); 

        Map<String, Object> stats = new HashMap<>();
        stats.put("summary", adMapper.selectAdSummary(partnerId));
        stats.put("dailyClicks", adMapper.selectDailyClickCounts(partnerId));
        stats.put("keywords", adMapper.selectKeywordDistribution(partnerId));
        stats.put("adList", adMapper.selectPartnerAdStatusList(partnerId));

        return ResponseEntity.ok(stats);
    }
}