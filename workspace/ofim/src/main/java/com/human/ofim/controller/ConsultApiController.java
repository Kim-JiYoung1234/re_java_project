package com.human.ofim.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import com.human.ofim.service.ConsultService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/consult")
public class ConsultApiController {

    private final ConsultService consultService;

    /**
     * 예약 가능 시간 슬롯 조회
     * ex) /api/consult/time-slots?expertId=1&date=2026-03-01&type=채팅
     */
    @GetMapping("/time-slots")
    public List<String> getTimeSlots(
        @RequestParam("expertId") Long expertId,
        @RequestParam("date") String date,
        @RequestParam("type") String consultType
    ) {
        return consultService.getAvailableTimeSlots(expertId, date, consultType);
    }
}