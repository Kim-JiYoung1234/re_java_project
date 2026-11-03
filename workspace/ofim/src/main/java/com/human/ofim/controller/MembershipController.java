package com.human.ofim.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.human.ofim.dto.MemberDto;
import com.human.ofim.service.MembershipService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MembershipController {

    private final MembershipService membershipService;

    @GetMapping("/mypage/membership")
    public String membership(HttpSession session, Model model) {
        MemberDto loginUser = (MemberDto) session.getAttribute("user");
        if (loginUser == null) {
            return "redirect:/auth/login";
        }

        model.addAttribute("membershipStatus",
                membershipService.getMembershipStatus(loginUser.getMemberId()));
        model.addAttribute("membershipUsage",
                membershipService.getMembershipUsage(loginUser.getMemberId()));

        return "mypage/membership";
    }
}