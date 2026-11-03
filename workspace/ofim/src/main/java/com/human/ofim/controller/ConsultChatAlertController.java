package com.human.ofim.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.human.ofim.dto.ConsultChatAlertDto;
import com.human.ofim.dto.MemberDto;
import com.human.ofim.service.ConsultChatService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/consult/chat")
public class ConsultChatAlertController {

    private final ConsultChatService consultChatService;

    @GetMapping("/expert/latest-alert")
    public ConsultChatAlertDto latestAlert(HttpSession session) {
        MemberDto loginUser = (MemberDto) session.getAttribute("loginUser");
        if (loginUser == null) {
            loginUser = (MemberDto) session.getAttribute("user");
        }

        if (loginUser == null) {
            ConsultChatAlertDto dto = new ConsultChatAlertDto();
            dto.setNewChat(false);
            return dto;
        }

        ConsultChatAlertDto dto = consultChatService.getLatestChatAlert(loginUser.getMemberId());

        if (dto == null) {
            dto = new ConsultChatAlertDto();
            dto.setNewChat(false);
        }

        return dto;
    }
}