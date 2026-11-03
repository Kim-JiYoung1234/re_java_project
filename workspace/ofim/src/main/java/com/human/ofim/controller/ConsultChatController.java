package com.human.ofim.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.human.ofim.dto.ConsultChatMessageDto;
import com.human.ofim.dto.ConsultChatRoomDto;
import com.human.ofim.dto.ConsultPendingDto;
import com.human.ofim.dto.MemberDto;
import com.human.ofim.service.ConsultChatService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/consult/chat")
public class ConsultChatController {

    private final ConsultChatService consultChatService;

    private MemberDto getLoginUser(HttpSession session) {
        MemberDto loginUser = (MemberDto) session.getAttribute("loginUser");
        if (loginUser == null) {
            loginUser = (MemberDto) session.getAttribute("user");
        }
        return loginUser;
    }

    @ResponseBody
    @PostMapping("/start")
    public String startChat(@RequestParam("expertId") Long expertId,
                            HttpSession session) {

        MemberDto loginUser = getLoginUser(session);
        if (loginUser == null) {
            throw new RuntimeException("로그인이 필요합니다.");
        }

        int consultId = consultChatService.findOrCreateChatConsult(loginUser.getMemberId(), expertId);
        return String.valueOf(consultId);
    }

    @ResponseBody
    @GetMapping("/poll")
    public ConsultPendingDto getPendingConsult(HttpSession session) {
        MemberDto loginUser = getLoginUser(session);
        if (loginUser == null) {
            throw new RuntimeException("로그인이 필요합니다.");
        }

        ConsultPendingDto dto = consultChatService.getPendingConsult(loginUser.getMemberId());
        return dto != null ? dto : new ConsultPendingDto();
    }

    @GetMapping("/room/{consultId}")
    public String chatRoomPage(@PathVariable("consultId") int consultId,
                               HttpSession session,
                               Model model) {

        MemberDto loginUser = getLoginUser(session);
        if (loginUser == null) {
            return "redirect:/auth/login";
        }

        Long loginMemberId = loginUser.getMemberId();

        if (!consultChatService.isParticipant(consultId, loginMemberId)) {
            return "redirect:/consult/history";
        }

        ConsultChatRoomDto room = consultChatService.getRoomSummary(consultId, loginMemberId);

        model.addAttribute("consultId", consultId);
        model.addAttribute("canChat", consultChatService.canChat(consultId, loginMemberId));
        model.addAttribute("room", room);
        model.addAttribute("loginMemberId", loginMemberId);
        model.addAttribute("readonly", false);

        return "consult/chat-room";
    }

    @ResponseBody
    @GetMapping("/room/{consultId}/messages")
    public List<ConsultChatMessageDto> getMessages(@PathVariable("consultId") int consultId,
                                                   HttpSession session) {

        MemberDto loginUser = getLoginUser(session);
        if (loginUser == null) {
            throw new RuntimeException("로그인이 필요합니다.");
        }

        if (!consultChatService.isParticipant(consultId, loginUser.getMemberId())) {
            throw new RuntimeException("열람 권한이 없습니다.");
        }

        return consultChatService.getMessages(consultId, loginUser.getMemberId());
    }

    @ResponseBody
    @PostMapping("/room/{consultId}/read")
    public Map<String, Object> readMessages(@PathVariable("consultId") int consultId,
                                            HttpSession session) {

        MemberDto loginUser = getLoginUser(session);
        if (loginUser == null) {
            throw new RuntimeException("로그인이 필요합니다.");
        }

        if (!consultChatService.isParticipant(consultId, loginUser.getMemberId())) {
            throw new RuntimeException("열람 권한이 없습니다.");
        }

        consultChatService.readMessages(consultId, loginUser.getMemberId());

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        return result;
    }

    @ResponseBody
    @GetMapping("/room/{consultId}/summary")
    public ConsultChatRoomDto getSummary(@PathVariable("consultId") int consultId,
                                         HttpSession session) {

        MemberDto loginUser = getLoginUser(session);
        if (loginUser == null) {
            throw new RuntimeException("로그인이 필요합니다.");
        }

        if (!consultChatService.isParticipant(consultId, loginUser.getMemberId())) {
            throw new RuntimeException("열람 권한이 없습니다.");
        }

        return consultChatService.getRoomSummary(consultId, loginUser.getMemberId());
    }

    @PostMapping("/accept")
    @ResponseBody
    public String acceptChat(@RequestParam("consultId") int consultId,
                             HttpSession session) {

        MemberDto loginUser = getLoginUser(session);
        if (loginUser == null) {
            return "login";
        }

        try {
            consultChatService.acceptChat(consultId, loginUser.getMemberId());
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }

    @PostMapping("/reject")
    @ResponseBody
    public String rejectChat(@RequestParam("consultId") int consultId,
                             @RequestParam("reason") String reason,
                             HttpSession session) {

        MemberDto loginUser = getLoginUser(session);
        if (loginUser == null) {
            return "login";
        }

        try {
            consultChatService.rejectChat(consultId, loginUser.getMemberId(), reason);
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }

    @PostMapping("/complete")
    @ResponseBody
    public String completeChat(@RequestParam("consultId") int consultId,
                               HttpSession session) {

        MemberDto loginUser = getLoginUser(session);
        if (loginUser == null) {
            return "login";
        }

        try {
            consultChatService.completeChat(consultId, loginUser.getMemberId());
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }
}