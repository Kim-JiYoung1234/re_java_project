package com.human.ofim.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.human.ofim.dto.FitnessConsultDto;
import com.human.ofim.mapper.FitnessConsultMapper;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/fitness")
@RequiredArgsConstructor
public class FitnessConsultController {

    private final FitnessConsultMapper fitnessConsultMapper;

    @PostMapping("/consult")
    public Map<String, Object> saveConsult(@RequestBody FitnessConsultDto dto) {
        Map<String, Object> result = new HashMap<>();
        try {
            fitnessConsultMapper.insertConsult(dto);
            result.put("success", true);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
        }
        return result;
    }

    @PostMapping("/stats/phone-copy")
    public Map<String, Object> updatePhoneStats(@RequestParam("centerNo") int centerNo) {
        Map<String, Object> result = new HashMap<>();
        try {
            fitnessConsultMapper.upsertPhoneStats(centerNo);
            result.put("success", true);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
        }
        return result;
    }

    @GetMapping("/partner/consults")
    public Map<String, Object> getPartnerConsults(HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        try {
            Object userObj = session.getAttribute("user");
            if (userObj == null) {
                result.put("success", false);
                return result;
            }

            Object idObj = userObj.getClass().getMethod("getMemberId").invoke(userObj);
            int partnerId = Integer.parseInt(String.valueOf(idObj));
            
            result.put("success", true);
            result.put("stats", Map.of(
                "phoneCopyCount", fitnessConsultMapper.selectTodayPhoneCopyCount(partnerId),
                "newConsultCount", fitnessConsultMapper.selectNewConsultCount(partnerId)
            ));
            result.put("list", fitnessConsultMapper.selectConsultListByPartnerId(partnerId));
            result.put("phoneTrend", fitnessConsultMapper.selectPhoneCopyTrend(partnerId));
            result.put("consultTrend", fitnessConsultMapper.selectConsultTrend(partnerId));

        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
        }
        return result;
    }

    @PostMapping("/partner/consults/status")
    public Map<String, Object> updateConsultStatus(@RequestParam("consultNo") int consultNo, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        try {
            Object userObj = session.getAttribute("user");
            if (userObj == null) {
                result.put("success", false);
                return result;
            }

            int partnerId = Integer.parseInt(String.valueOf(userObj.getClass().getMethod("getMemberId").invoke(userObj)));
            fitnessConsultMapper.updateConsultStatus(consultNo, partnerId);
            result.put("success", true);

        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
        }
        return result;
    }

    // [신규] 파트너 메모 저장 API
    @PostMapping("/partner/consults/memo")
    public Map<String, Object> updateConsultMemo(@RequestParam("consultNo") int consultNo, @RequestParam("partnerMemo") String partnerMemo, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        try {
            Object userObj = session.getAttribute("user");
            if (userObj == null) {
                result.put("success", false);
                return result;
            }

            int partnerId = Integer.parseInt(String.valueOf(userObj.getClass().getMethod("getMemberId").invoke(userObj)));
            fitnessConsultMapper.updateConsultMemo(consultNo, partnerId, partnerMemo);
            result.put("success", true);

        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
        }
        return result;
    }
}