package com.human.ofim.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.bind.annotation.ResponseBody;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.human.ofim.dto.ConsultChatMessageDto;
import com.human.ofim.dto.ConsultDto;
import com.human.ofim.dto.InquiryDto;
import com.human.ofim.dto.MemberDto;
import com.human.ofim.service.ConsultService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequestMapping("/consult")
@RequiredArgsConstructor
public class ConsultController {

    private final ConsultService consultService;

    @GetMapping("/list")
    public String list(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "specialty", required = false) String specialty,
            @RequestParam(value = "sort", defaultValue = "latest") String sort,
            @RequestParam(value = "onlineOnly", required = false) Boolean onlineOnly,
            @RequestParam(value = "page", defaultValue = "1") int page,
            Model model) {

        int size = 6;

        List<ConsultDto> expertList =
                consultService.getExpertList(keyword, specialty, sort, onlineOnly, page, size);

        long totalCount = consultService.getTotalCount(keyword, specialty, onlineOnly);
        int totalPages = (int) Math.ceil((double) totalCount / size);

        model.addAttribute("expertList", expertList);
        model.addAttribute("specialtyOptions", consultService.getSpecialtyOptions());
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", page);
        model.addAttribute("noResult", expertList == null || expertList.isEmpty());

        return "consult/list";
    }

    @GetMapping("/detail")
    public String detail(@RequestParam("expertId") Long expertId, Model model) {

        ConsultDto expert = consultService.getExpertDetail(expertId);

        if (expert == null) {
            return "redirect:/consult/list";
        }

        expert.setCareerList(consultService.getCareerList(expertId));
        expert.setEducationList(consultService.getEducationList(expertId));
        expert.setCertificateList(consultService.getCertificateList(expertId));

        Map<String, Object> reviewSummary = consultService.getReviewSummary(expertId);

        if (reviewSummary != null) {
            Object avg = reviewSummary.get("avgRating");
            Object cnt = reviewSummary.get("reviewCount");

            expert.setAvgRating(avg == null ? 0.0 : ((Number) avg).doubleValue());
            expert.setReviewCount(cnt == null ? 0 : ((Number) cnt).intValue());
        } else {
            expert.setAvgRating(0.0);
            expert.setReviewCount(0);
        }

        model.addAttribute("expert", expert);
        return "consult/detail";
    }

    @GetMapping("/reserve")
    public String reserve(
            @RequestParam("expertId") Long expertId,
            @RequestParam(value = "type", required = false, defaultValue = "채팅") String type,
            Model model,
            HttpSession session,
            RedirectAttributes ra) {

        MemberDto loginMember = (MemberDto) session.getAttribute("user");
        if (loginMember == null) {
            ra.addFlashAttribute("errorMessage", "로그인이 필요합니다.");
            return "redirect:/auth/login";
        }

        Map<String, Object> expert = consultService.getReserveExpertInfo(expertId);

        if (expert == null) {
            ra.addFlashAttribute("errorMessage", "전문가 정보를 찾을 수 없습니다.");
            return "redirect:/consult/list";
        }

        model.addAttribute("expert", expert);
        model.addAttribute("type", type);
        model.addAttribute("editMode", false);

        return "consult/reserve";
    }

    @GetMapping("/reserve/edit")
    public String editReserve(
            @RequestParam("consultId") Long consultId,
            HttpSession session,
            Model model,
            RedirectAttributes ra) {

        MemberDto loginMember = (MemberDto) session.getAttribute("user");
        if (loginMember == null) {
            ra.addFlashAttribute("errorMessage", "로그인이 필요합니다.");
            return "redirect:/auth/login";
        }

        Map<String, Object> reservation =
                consultService.getReservationDetailForEdit(consultId, loginMember.getMemberId());

        if (reservation == null) {
            ra.addFlashAttribute("errorMessage", "예약 정보를 찾을 수 없습니다.");
            return "redirect:/consult/history";
        }

        model.addAttribute("expert", reservation);
        model.addAttribute("type", reservation.get("consult_type"));
        model.addAttribute("reservation", reservation);
        model.addAttribute("editMode", true);

        return "consult/reserve";
    }

    @PostMapping("/reserve/submit")
    public String reserveSubmit(
            @RequestParam(value = "consultId", required = false) Long consultId,
            @RequestParam("expertId") Long expertId,
            @RequestParam("consultType") String consultType,
            @RequestParam("reserveDate") String reserveDate,
            @RequestParam("reserveTime") String reserveTime,
            @RequestParam(value = "consultContent", required = false) String consultContent,
            @RequestParam(value = "contactPhone", required = false) String contactPhone,
            HttpSession session,
            RedirectAttributes ra) {

        MemberDto loginMember = (MemberDto) session.getAttribute("user");
        if (loginMember == null) {
            ra.addFlashAttribute("errorMessage", "로그인이 필요합니다.");
            return "redirect:/auth/login";
        }

        try {
            if (consultId != null) {
                consultService.updateReservation(
                        consultId,
                        loginMember.getMemberId(),
                        expertId,
                        consultType,
                        reserveDate,
                        reserveTime,
                        consultContent,
                        contactPhone
                );
                ra.addFlashAttribute("successMessage", "예약이 변경되었습니다.");
            } else {
                consultService.reserveConsult(
                        loginMember.getMemberId(),
                        expertId,
                        consultType,
                        reserveDate,
                        reserveTime,
                        consultContent,
                        contactPhone
                );
                ra.addFlashAttribute("successMessage", "상담 예약이 완료되었습니다.");
            }

            return "redirect:/consult/history";

        } catch (Exception e) {
            e.printStackTrace();
            ra.addFlashAttribute("errorMessage", e.getMessage());

            if (consultId != null) {
                ra.addAttribute("consultId", consultId);
                return "redirect:/consult/reserve/edit";
            }

            ra.addAttribute("expertId", expertId);
            ra.addAttribute("type", consultType);
            return "redirect:/consult/reserve";
        }
    }

    @GetMapping("/history")
    public String history(
            @RequestParam(value = "expertName", required = false) String expertName,
            @RequestParam(value = "consultType", required = false) String consultType,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "dateFrom", required = false) String dateFrom,
            @RequestParam(value = "dateTo", required = false) String dateTo,
            HttpSession session,
            Model model) {

        MemberDto loginUser = (MemberDto) session.getAttribute("user");
        if (loginUser == null) {
            return "redirect:/auth/login";
        }

        List<Map<String, Object>> reservations = consultService.getConsultHistory(
                loginUser.getMemberId(),
                expertName,
                consultType,
                status,
                dateFrom,
                dateTo
        );

        model.addAttribute("reservations", reservations);
        return "consult/history";
    }

    @PostMapping("/cancel")
    public String cancelReservation(
            @RequestParam("consultId") Long consultId,
            HttpSession session,
            RedirectAttributes ra) {

        MemberDto loginMember = (MemberDto) session.getAttribute("user");
        if (loginMember == null) {
            return "redirect:/auth/login";
        }

        try {
            consultService.cancelReservation(consultId, loginMember.getMemberId());
            ra.addFlashAttribute("successMessage", "예약이 취소되었습니다.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/consult/history";
    }

    @GetMapping("/emailform")
    public String emailForm(
            @RequestParam("expertId") Long expertId,
            @RequestParam(value = "type", required = false, defaultValue = "1:1문의") String type,
            @RequestParam(value = "presetTitle", required = false) String presetTitle,
            Model model,
            HttpSession session,
            RedirectAttributes ra) {

        MemberDto loginMember = (MemberDto) session.getAttribute("user");
        if (loginMember == null) {
            ra.addFlashAttribute("errorMessage", "로그인이 필요합니다.");
            return "redirect:/auth/login";
        }

        ConsultDto expert = consultService.getExpertDetail(expertId);
        if (expert == null) {
            ra.addFlashAttribute("errorMessage", "전문가 정보를 찾을 수 없습니다.");
            return "redirect:/consult/list";
        }

        model.addAttribute("expert", expert);
        model.addAttribute("type", type);
        model.addAttribute("presetTitle", presetTitle);

        return "consult/emailform";
    }
    
    @PostMapping("/updateStatus")
    @ResponseBody // 화면 이동 없이 데이터만 응답하기 위해 꼭 필요합니다!
    public String updateConsultStatus(
            @RequestParam("consultId") Long consultId,
            @RequestParam("status") Integer status) {

        try {
            consultService.updateConsultStatus(consultId, status);
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }

    @PostMapping("/emailform/submit")
    public String submitEmailForm(
            @RequestParam("expertId") Long expertId,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "consultType", required = false, defaultValue = "1:1문의") String consultType,
            @RequestParam(value = "files", required = false) MultipartFile[] files,
            HttpSession session,
            RedirectAttributes ra) {

        MemberDto loginMember = (MemberDto) session.getAttribute("user");
        if (loginMember == null) {
            ra.addFlashAttribute("errorMessage", "로그인이 필요합니다.");
            return "redirect:/auth/login";
        }

        try {
            consultService.insertInquiry(
                    loginMember.getMemberId(),
                    expertId,
                    consultType,
                    title,
                    content,
                    files
            );

            ra.addFlashAttribute("successMessage", "1:1 문의가 등록되었습니다.");
            return "redirect:/consult/history";

        } catch (Exception e) {
            e.printStackTrace();
            ra.addFlashAttribute("errorMessage", "1:1 문의 등록 중 오류가 발생했습니다.");
            ra.addAttribute("expertId", expertId);
            ra.addAttribute("type", consultType);
            ra.addAttribute("presetTitle", title);
            return "redirect:/consult/emailform";
        }
    }
    
    @PostMapping("/expert/inquiry/answer")
    @ResponseBody
    public String submitInquiryAnswer(@RequestParam("consult_id") Long consultId, 
                                      @RequestParam("answer") String answer) {
        
        boolean isSuccess = consultService.submitExpertAnswer(consultId, answer);
        
        if (isSuccess) {
            return "success";
        } else {
            return "fail";
        }
    }
    
   
    @PostMapping("/cancel-ajax")
    @ResponseBody
    public String cancelMyConsult(@RequestParam("consultId") Long consultId, HttpSession session) {
        MemberDto loginUser = (MemberDto) session.getAttribute("user");

        if (loginUser == null) {
            return "fail";
        }

        try {
            consultService.cancelReservation(consultId, loginUser.getMemberId());
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }
    
    @GetMapping("/chat/history/{consultId}")
    public String chatHistory(
            @PathVariable("consultId") Long consultId,
            HttpSession session,
            Model model,
            RedirectAttributes ra) {

        MemberDto loginUser = (MemberDto) session.getAttribute("user");
        if (loginUser == null) {
            ra.addFlashAttribute("errorMessage", "로그인이 필요합니다.");
            return "redirect:/auth/login";
        }

        Map<String, Object> consult = consultService.getConsultByIdAndMember(consultId, loginUser.getMemberId());

        if (consult == null) {
            ra.addFlashAttribute("errorMessage", "상담 내역을 찾을 수 없거나 열람 권한이 없습니다.");
            return "redirect:/consult/history";
        }

        List<ConsultChatMessageDto> messages =
                consultService.getChatMessagesByConsultId(consultId);

        boolean canChat = false; // history 진입은 무조건 읽기 전용

        Map<String, Object> room = new HashMap<>();
        room.put("unreadCount", 0);

        model.addAttribute("consultId", consultId);
        model.addAttribute("loginMemberId", loginUser.getMemberId());
        model.addAttribute("messages", messages);
        model.addAttribute("canChat", canChat);
        model.addAttribute("readonly", true);
        model.addAttribute("room", room);

        return "consult/chat-room";
    }
    @GetMapping("/call-note")
    @ResponseBody
    public InquiryDto getCallNote(@RequestParam("consultId") Long consultId) {
        return consultService.getCallNote(consultId);
    }
    
    @PostMapping("/call-note/save")
    @ResponseBody
    public String saveCallNote(
            @RequestParam("consultId") Long consultId,
            @RequestParam(value = "content", required = false) String content,
            @RequestParam(value = "file", required = false) MultipartFile file,
            HttpSession session
    ) {

        MemberDto loginUser = (MemberDto) session.getAttribute("user");

        if (loginUser == null) {
            return "login";
        }

        try {
            consultService.saveCallNote(
                    consultId,
                    loginUser.getMemberId(),
                    content,
                    file
            );
            return "success";

        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }
    
}