package com.human.ofim.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.human.ofim.dto.AdminConsultDto;
import com.human.ofim.service.AdminConsultService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AdminConsultController {

    private final AdminConsultService adminConsultService;

    @GetMapping("/admin/consulting/list")
    public String consultingList(
            @RequestParam(value = "consultType", required = false) String consultType,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "delayStatus", required = false) String delayStatus,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam(value = "searchType", required = false, defaultValue = "all") String searchType,
            @RequestParam(value = "keyword", required = false) String keyword,
            Model model
    ) {

        List<AdminConsultDto> consultList = adminConsultService.getConsultList(
                consultType, status, delayStatus, startDate, endDate, searchType, keyword
        );

        model.addAttribute("consultList", consultList);

        model.addAttribute("totalConsult", adminConsultService.getTotalConsult());
        model.addAttribute("completedConsult", adminConsultService.getCompletedConsult());
        model.addAttribute("completedRate", adminConsultService.getCompletedRate());
        model.addAttribute("waitingConsult", adminConsultService.getWaitingConsult());
        model.addAttribute("delayConsult", adminConsultService.getDelayConsult());
        model.addAttribute("delayRate", adminConsultService.getDelayRate());
        model.addAttribute("todayReceived", adminConsultService.getTodayReceived());
        model.addAttribute("todayComplete", adminConsultService.getTodayComplete());

        model.addAttribute("consultType", consultType);
        model.addAttribute("status", status);
        model.addAttribute("delayStatus", delayStatus);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("searchType", searchType);
        model.addAttribute("keyword", keyword);

        model.addAttribute("pageTitle", "상담 현황 관리");
        model.addAttribute("activeMenu", "consulting");
        model.addAttribute("expertOptions", List.of());
        
        return "admin/consulting_list";
    }
    
    @PostMapping("/admin/consulting/reply")
    @ResponseBody
    public String saveAdminReply(@RequestBody AdminConsultDto dto) {

        adminConsultService.saveAdminReply(dto);

        return "success";
    }
    
    @PostMapping("/admin/consulting/update")
    @ResponseBody
    public String updateConsult(@RequestBody AdminConsultDto dto) {

        adminConsultService.updateConsult(dto);

        return "success";
    }
    
}