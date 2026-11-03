package com.human.ofim.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.human.ofim.dto.BoardDto;
import com.human.ofim.dto.MemberDto;
import com.human.ofim.service.AdminUserService;
import com.human.ofim.service.BoardService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;
    private final BoardService boardService;
    
    @GetMapping
    public String userList(
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "grade", required = false) String grade,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "1") int page,
            Model model
    ) {
        if (page < 1) page = 1;

        int size = 10;
        int offset = (page - 1) * size;

        List<MemberDto> users = adminUserService.getUserList(type, grade, status, keyword, offset, size);
        int totalCount = adminUserService.getUserCount(type, grade, status, keyword);
        int totalPages = (int) Math.ceil((double) totalCount / size);

        model.addAttribute("users", users);
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("totalPages", totalPages);

        model.addAttribute("type", type);
        model.addAttribute("grade", grade);
        model.addAttribute("status", status);
        model.addAttribute("keyword", keyword);

        model.addAttribute("pageTitle", "회원 관리");
        model.addAttribute("pageDesc", "");
        model.addAttribute("activeMenu", "users");

        return "admin/users";
    }

    @GetMapping("/{memberId}")
    @ResponseBody
    public ResponseEntity<MemberDto> getUserDetail(@PathVariable("memberId") Long memberId) {
        MemberDto user = adminUserService.getUserDetail(memberId);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    @PostMapping("/status")
    public ResponseEntity<Map<String, Object>> updateMemberStatus(
            @RequestParam("memberId") int memberId,
            @RequestParam("memberStatus") String memberStatus,
            @RequestParam(value = "reason", required = false) String reason) {

        Map<String, Object> result = new HashMap<>();

        try {
            adminUserService.updateMemberStatus(memberId, memberStatus, reason);
            result.put("success", true);
            result.put("message", "회원 상태가 변경되었습니다.");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    @GetMapping("/{memberId}/posts")
    @ResponseBody
    public List<BoardDto> getUserPosts(@PathVariable("memberId") Long memberId) {
        return boardService.getMyPosts(memberId.intValue());
    }
}