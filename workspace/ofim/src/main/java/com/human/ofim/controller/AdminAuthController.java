package com.human.ofim.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminAuthController {

	// 1. 관리자 메인 진입점 (대시보드로 바로 연결)
	// - 인터셉터가 로그인 안 된 사람은 알아서 /admin/login 으로 튕겨냅니다.
	// - 로그인 된 사람만 이 메서드를 타게 되므로, 여기서 바로 대시보드 컨트롤러로 포워딩합니다.
	@GetMapping({ "", "/" })
	public String adminRoot() {
		return "forward:/admin/dashboard";
	}

	// 2. 관리자 로그인 화면 띄우기
	@GetMapping("/login")
	public String adminLoginForm() {
		return "admin/admin_login";
	}

	// 3. 관리자 로그인 처리
	@PostMapping("/login")
	public String adminLoginProcess(@RequestParam("adminId") String adminId, @RequestParam("adminPw") String adminPw,
			HttpSession session, Model model) {

		// 1. 아이디/비밀번호 체크
		if ("admin".equals(adminId) && "1234".equals(adminPw)) {

			// 2. 관리자 페이지용 세션 저장 (기존 코드)
			session.setAttribute("adminUser", "admin");

			// 3. [핵심] 커뮤니티 권한 및 아이콘 표시용 세션 저장
			// 일반 유저와 똑같은 'user'라는 키를 사용해 관리자 정보를 심습니다.
			com.human.ofim.dto.MemberDto adminAuth = new com.human.ofim.dto.MemberDto();
			adminAuth.setNickname("관리자");
			adminAuth.setMemberType("관리자"); // 이 텍스트가 "관리자"여야 아이콘이 뜹니다.
			adminAuth.setMemberId(1L); // 관리자 식별 번호 (임시 1번)

			session.setAttribute("user", adminAuth);

			// 4. 대시보드로 리다이렉트
			return "redirect:/admin";
		} else {
			model.addAttribute("error", "관리자 정보가 일치하지 않습니다.");
			return "admin/admin_login";
		}
	}

	// 4. 버튼 클릭 시 이동하는 일반 로그아웃 (GET)
	@GetMapping("/logout")
	public String adminLogout(HttpSession session) {
		session.removeAttribute("adminUser");
		session.removeAttribute("user"); // 커뮤니티 권한 세션도 삭제!
		return "redirect:/admin/login";
	}

	// 5. 브라우저 종료/새로고침 시 백그라운드에서 쏘는 강제 로그아웃 (POST)
	@PostMapping("/logout/auto")
	@ResponseBody
	public String adminAutoLogout(HttpSession session) {
		session.removeAttribute("adminUser");
		return "ok";
	}
}