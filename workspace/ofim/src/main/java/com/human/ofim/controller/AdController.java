package com.human.ofim.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.human.ofim.dto.AdRequestDto;
import com.human.ofim.dto.MemberDto;
import com.human.ofim.service.AdService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/ad")
public class AdController {

    @Autowired
    private AdService adService;

    @GetMapping("/register")
    public String showAdRegisterForm(HttpSession session, Model model, RedirectAttributes rttr) {
        MemberDto user = (MemberDto) session.getAttribute("user");
        if (user == null) {
            rttr.addFlashAttribute("msg", "로그인이 필요한 서비스입니다.");
            return "redirect:/auth/login"; 
        }
        Long partnerId = user.getMemberId(); 
        model.addAttribute("myCenters", adService.getPartnerCenters(partnerId));
        return "ad/ad_register";
    }

    @PostMapping("/pay")
    public String processPayment(AdRequestDto dto, HttpSession session, RedirectAttributes rttr) {
        MemberDto user = (MemberDto) session.getAttribute("user");
        if (user == null) return "redirect:/auth/login";
        
        try {
            adService.processAdPayment(dto, user.getMemberId());
            // [해결 1] 모달 문구를 명확하게 변경하여 오해 소지 차단
            rttr.addFlashAttribute("msg", "광고 입찰 접수가 완료되었습니다! 관리자 심사 승인 후 최종 노출됩니다.");
        } catch (Exception e) {
            e.printStackTrace();
            rttr.addFlashAttribute("msg", "접수 처리 중 에러가 발생했습니다.");
        }
        return "redirect:/home"; 
    }

    @GetMapping("/status")
    public String showPartnerAdStatus(HttpSession session, Model model) {
        MemberDto user = (MemberDto) session.getAttribute("user");
        if (user == null) return "redirect:/auth/login";
        
        model.addAttribute("adList", adService.getPartnerAdStatusList(user.getMemberId()));
        model.addAttribute("userType", "파트너회원");
        model.addAttribute("activeTab", "ad");
        
        return "mypage/main";
    }

    @GetMapping("/revenue")
    public String showRevenueDashboard(Model model) {
        model.addAttribute("revenueList", adService.getRevenueList());
        model.addAttribute("totalRevenue", adService.getTotalRevenue());
        return "ad/ad_revenue";
    }
}