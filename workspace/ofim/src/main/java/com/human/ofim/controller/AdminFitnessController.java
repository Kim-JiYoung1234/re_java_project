package com.human.ofim.controller;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.human.ofim.dto.FitnessCenterDto;
import com.human.ofim.dto.PartnerUserDto;
import com.human.ofim.service.AdminFitnessService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminFitnessController {

    private final AdminFitnessService adminFitnessService;

    @GetMapping("/fitness/list")
    public String list(@RequestParam(value="memberId", required=false) Long memberId,
                       @RequestParam(value="keyword", required=false) String keyword,
                       Model model) {

        List<PartnerUserDto> allPartners = adminFitnessService.getAllPartnerList();
        List<PartnerUserDto> pendingPartners = new ArrayList<>();
        List<PartnerUserDto> approvedPartners = new ArrayList<>();
        
        for(PartnerUserDto p : allPartners) {
            // [핵심 수정] Integer 타입으로 0(대기)과 1(승인)을 정확히 분류합니다.
            if(p.getApproveStatus() == null || p.getApproveStatus() == 0) {
                pendingPartners.add(p);
            } else if(p.getApproveStatus() == 1) {
                approvedPartners.add(p);
            }
        }

        model.addAttribute("pendingPartners", pendingPartners);
        model.addAttribute("approvedPartners", approvedPartners);

        List<FitnessCenterDto> centerList = adminFitnessService.getCenterList(memberId, keyword);
        model.addAttribute("list", centerList != null ? centerList : new ArrayList<>());

        model.addAttribute("memberId", memberId);
        model.addAttribute("keyword", keyword);
        model.addAttribute("pageTitle", "파트너사 관리");
        model.addAttribute("activeMenu", "fitness");

        // [수정 전] return "admin/fitness/list";
        // [수정 후] 껍데기(레이아웃)를 부르도록 변경합니다.
        return "admin/layout";
    }

    @PostMapping("/partner/approve")
    public String approvePartner(@RequestParam("memberId") Long memberId, 
                                 @RequestParam("approveStatus") Integer approveStatus) {
        adminFitnessService.updatePartnerApproveStatus(memberId, approveStatus);
        return "redirect:/admin/fitness/list";
    }

    @PostMapping("/fitness/approve")
    public String approveCenter(@RequestParam("centerNo") Integer centerNo, 
                                @RequestParam("approveStatus") Integer approveStatus) {
        adminFitnessService.updateApproveStatus(centerNo, approveStatus);
        return "redirect:/admin/fitness/list";
    }

    @PostMapping("/fitness/delete")
    public String deleteCenter(@RequestParam("centerNo") Integer centerNo) {
        adminFitnessService.deleteCenter(centerNo);
        return "redirect:/admin/fitness/list";
    }

    @GetMapping("/fitness/detail")
    public String centerDetail(@RequestParam("centerNo") Integer centerNo, Model model) {
        model.addAttribute("center", adminFitnessService.getCenterDetail(centerNo));
        model.addAttribute("pageTitle", "시설 상세 정보");
        model.addAttribute("activeMenu", "fitness_detail");
        return "admin/layout";
    }
}