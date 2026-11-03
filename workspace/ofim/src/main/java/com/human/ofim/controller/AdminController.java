package com.human.ofim.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.human.ofim.service.AdService;
import com.human.ofim.service.AdminExpertService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdService adService;
    private final AdminExpertService adminExpertService;

    public AdminController(AdService adService,
    		 			   AdminExpertService adminExpertService) {
        this.adService = adService;
        this.adminExpertService = adminExpertService;
    }

    private String render(Model model, String viewName, String pageTitle, String pageDesc, String activeMenu) {
        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("pageDesc", pageDesc);
        model.addAttribute("activeMenu", activeMenu);
        return viewName;
    }

    // 회원관리는 AdminUserController가 담당
    // @GetMapping("/users") 제거

    @GetMapping("/fitness")
    public String fitnessRedirect() {
        return "redirect:/admin/fitness/list";
    }

    @GetMapping("/expert")
    public String expertRedirect() {
        return "redirect:/admin/expert/list";
    }

    @GetMapping("/pending")
    public String pendingRedirect() {
        return "redirect:/admin/expert/list?status=0";
    }

    @GetMapping("/settlements")
    public String settlements(Model model) {
        model.addAttribute("revenueList", adService.getRevenueList());
        model.addAttribute("totalRevenue", adService.getTotalRevenue());
        return render(model, "admin/settlements", "정산 관리", "플랫폼 실시간 수익", "settlements");
    }

    @GetMapping("/expert-settlements")
    public String expertSettlements(Model model) {
        model.addAttribute("pageTitle", "전문가 정산 관리");
        model.addAttribute("activeMenu", "expert_settlement");
        model.addAttribute("settlementList",
                adminExpertService.getSettlementList(null));
        return "admin/layout";
    }
    
    @GetMapping("/expert-settlements/detail")
    public String expertSettlementDetail(
            @RequestParam("expertId") Long expertId,
            @RequestParam("settlementMonth") String settlementMonth,
            Model model) {

        // 상세 헤더 (총합, 계좌 등)
        model.addAttribute("header",
                adminExpertService.getSettlementHeader(expertId, settlementMonth));

        // 상세 리스트 (상담 목록)
        model.addAttribute("detailList",
                adminExpertService.getSettlementDetail(expertId, settlementMonth));

        model.addAttribute("expertId", expertId);
        model.addAttribute("settlementMonth", settlementMonth);

        model.addAttribute("pageTitle", "전문가 정산 상세");
        model.addAttribute("activeMenu", "expert_settlement_detail");

        return "admin/layout";
    }
    
    @GetMapping("/ad-review")
    public String adReviewList(Model model) {
        model.addAttribute("pendingAds", adService.getPendingAds());
        return render(model, "admin/ad_review", "광고 심사 관리", "입찰가가 높은 순서대로 우선 심사를 진행합니다.", "ad_review");
    }

    @PostMapping("/ad-approve")
    public String approveAd(@RequestParam("adId") Integer adId,
                            @RequestParam("centerNo") Integer centerNo) {
        adService.approveAd(adId, centerNo);
        return "redirect:/admin/ad-review";
    }

    @PostMapping("/ad-reject")
    public String rejectAd(@RequestParam("adId") Integer adId) {
        adService.rejectAd(adId);
        return "redirect:/admin/ad-review";
    }
    
  
}