package com.human.ofim.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.human.ofim.dto.MemberDto;
import com.human.ofim.service.GradeService;

import jakarta.servlet.http.HttpSession;

@Controller
public class GradeController {
	
	GradeService gradeService;

	public GradeController(GradeService gradeService) {
		this.gradeService = gradeService;
	}

	@PostMapping("/membership")
	public String membership(@RequestParam("planName") String planName, HttpSession session) {
		// 현재 로그인한 ID를 가져옴
		
		System.out.println("planName = " + planName);

		MemberDto loginUser = (MemberDto) session.getAttribute("user");
		long memberId = loginUser.getMemberId();

		// 2. DB에서 해당 유저의 멤버십 등급을 planName으로 업데이트 (Service 호출)
		gradeService.updateMembership(memberId, planName);
		// 3. 완료 후 결과 페이지나 메인으로 리다이렉트
		System.out.println("선택된 등급: " + planName);

		return "redirect:/home";
	}


}
