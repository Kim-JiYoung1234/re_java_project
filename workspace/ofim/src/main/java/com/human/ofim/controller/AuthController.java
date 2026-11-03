package com.human.ofim.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.human.ofim.dto.CertificateDto;
import com.human.ofim.dto.EmailAuthDto;
import com.human.ofim.dto.ExpertInfoDto;
import com.human.ofim.dto.ExpertUserDto;
import com.human.ofim.dto.HealthInfoDto;
import com.human.ofim.dto.MemberDto;
import com.human.ofim.dto.PartnerUserDto;
import com.human.ofim.mapper.EmailAuthMapper;
import com.human.ofim.service.AuthService;
import com.human.ofim.service.EmailService;
import com.human.ofim.service.ExpertService;

import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {

	@Autowired
	private AuthService authService;

	@Autowired
	private ExpertService expertService;

	@Autowired
	private EmailService emailService;
	
	@Autowired
	private EmailAuthMapper emailAuthMapper;

	// =====================================================================
	// 🌟 [수정된 부분] 데이터 없는 home.html 껍데기만 여는 것을 방지!
	// 데이터가 꽉 찬 진짜 HomeController의 /home 경로로 강제 이동시킵니다.
	// =====================================================================
	@GetMapping("/auth/login")
	public String loginForm(RedirectAttributes rttr) {
		// 홈 화면 스크립트에게 "이 유저는 로그인 하려고 온거야!" 라고 신호를 보냄
		rttr.addFlashAttribute("loginRequired", true);
		return "redirect:/home";
	}

	@GetMapping(path = { "/auth/register" })
	public String registerForm() {
		return "auth/register";
	}

	@PostMapping("/auth/register")
	public String register(MemberDto memberDto, RedirectAttributes rttr) {
		try {
			memberDto.setMemberType("일반회원");
			authService.register(memberDto);
			rttr.addFlashAttribute("msg", "일반 회원가입이 완료되었습니다.");
			return "redirect:/home";
		} catch (IllegalArgumentException e) {
			rttr.addFlashAttribute("msg", e.getMessage()); 
			return "redirect:/auth/register"; 
		}
	}

	@PostMapping("/register/partner")
	public String registerPartner(MemberDto memberDto, PartnerUserDto partnerUserDto, RedirectAttributes rttr) {
		try {
			memberDto.setMemberType("파트너회원");
			authService.registerPartnerMember(memberDto, partnerUserDto);
			rttr.addFlashAttribute("msg", "파트너 가입이 완료되었습니다. 관리자 승인 후 로그인 가능합니다.");
			return "redirect:/home";
		} catch (IllegalArgumentException e) {
			rttr.addFlashAttribute("msg", e.getMessage());
			return "redirect:/auth/register";
		} catch (RuntimeException e) {
			rttr.addFlashAttribute("msg", e.getMessage());
			return "redirect:/auth/register";
		}
	}

	@PostMapping("/register/expert")
	public String registerExpert(MemberDto memberDto, ExpertUserDto expertUserDto, CertificateDto certificateDto,
			RedirectAttributes rttr) {
		try {
			memberDto.setMemberType("전문가회원");
			authService.registerExpertMember(memberDto, expertUserDto, certificateDto);
			rttr.addFlashAttribute("msg", "전문가 회원가입이 완료되었습니다. 관리자 승인 후 로그인 가능합니다.");
			return "redirect:/home";
		} catch (IllegalArgumentException e) {
			rttr.addFlashAttribute("msg", e.getMessage());
			return "redirect:/auth/register";
		} catch (RuntimeException e) {
			rttr.addFlashAttribute("msg", e.getMessage());
			return "redirect:/auth/register";
		}
	}

	@GetMapping("/auth/check-id")
	@ResponseBody
	public boolean checkId(@RequestParam("loginId") String loginId) {
		return authService.isIdDuplicate(loginId);
	}

	@GetMapping("/auth/check-nickname")
	@ResponseBody
	public boolean checkNickname(@RequestParam("nickname") String nickname) {
		return authService.isNicknameDuplicate(nickname);
	}

	@GetMapping("/auth/check-email")
	@ResponseBody
	public boolean checkEmail(@RequestParam("email") String email) {
		return authService.isEmailDuplicate(email);
	}

	@PostMapping("/auth/login")
	public String login(@RequestParam("loginId") String loginId, @RequestParam("password") String password,
			@RequestParam(value = "redirect", required = false) String redirect, HttpSession session,
			RedirectAttributes rttr) {

		try {
			MemberDto loginMember = authService.login(loginId, password);

			if (loginMember != null) {
				session.setAttribute("user", loginMember);
				session.setAttribute("loginUser", loginMember);

				if ("전문가회원".equals(loginMember.getMemberType())) {
					expertService.updateOnlineStatus(loginMember.getMemberId(), true);
				}

				if (redirect != null && !redirect.isEmpty()) {
					return "redirect:" + redirect;
				}
				return "redirect:/home";

			} else {
				rttr.addFlashAttribute("msg", "아이디 또는 비밀번호가 틀렸습니다.");
				if (redirect != null && !redirect.isEmpty()) {
					return "redirect:/home?redirect=" + redirect;
				}
				return "redirect:/home";
			}
		} catch (IllegalStateException e) {
			rttr.addFlashAttribute("msg", e.getMessage());
			return "redirect:/home";
		}
	}

	@GetMapping("/auth/logout")
	public String logout(HttpSession session, @RequestParam(value = "redirect", required = false) String redirect) {

		MemberDto loginUser = (MemberDto) session.getAttribute("loginUser");
		if (loginUser == null) {
			loginUser = (MemberDto) session.getAttribute("user");
		}

		if (loginUser != null && "전문가회원".equals(loginUser.getMemberType())) {
			expertService.updateOnlineStatus(loginUser.getMemberId(), false);
		}

		session.invalidate();

		if (redirect != null && !redirect.isEmpty()) {
			return "redirect:" + redirect;
		}
		return "redirect:/home";
	}

	@PostMapping("/auth/send-reset-email")
	@ResponseBody
	public String sendResetEmail(@RequestParam("email") String email) {
		boolean isExist = authService.isEmailDuplicate(email);
		if (!isExist) {
			return "not_found"; 
		}
		try {
			emailService.sendPasswordResetEmail(email); 
			return "success";
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
	}

	@GetMapping("/auth/reset-password")
	public String showResetPasswordForm(@RequestParam("email") String email, 
										@RequestParam("token") String token,
										Model model, 
										RedirectAttributes rttr) {

		EmailAuthDto authDto = emailAuthMapper.getValidToken(email, token, "PW_RESET");

		if (authDto == null) {
			rttr.addFlashAttribute("msg", "유효하지 않거나 만료된 링크입니다. 다시 시도해 주세요.");
			return "redirect:/home";
		}

		model.addAttribute("email", email);
		model.addAttribute("token", token);

		return "auth/reset_password_form";
	}

	@PostMapping("/auth/reset-password")
	public String processResetPassword(@RequestParam("email") String email, 
									   @RequestParam("token") String token,
									   @RequestParam("newPassword") String newPassword, 
									   RedirectAttributes rttr) {

		EmailAuthDto authDto = emailAuthMapper.getValidToken(email, token, "PW_RESET");
		if (authDto == null) {
			rttr.addFlashAttribute("msg", "잘못된 접근입니다.");
			return "redirect:/home";
		}

		authService.updatePassword(email, newPassword);
		emailAuthMapper.updateTokenUsed(authDto.getAuthId());

		rttr.addFlashAttribute("msg", "비밀번호가 성공적으로 변경되었습니다. 새 비밀번호로 로그인해 주세요.");
		return "redirect:/home";
	}
	
    @PostMapping("/auth/find-id")
    @ResponseBody
    public String findLoginId(@RequestParam("userName") String userName,
                              @RequestParam("email") String email) {
        
        String loginId = authService.findIdByNameAndEmail(userName, email);
        
        if (loginId == null || loginId.isEmpty()) {
            return "not_found"; 
        }

        return loginId.substring(0, loginId.length() - 3) + "***";
    }
    
    @PostMapping("/auth/send-full-id")
    @ResponseBody
    public String sendFullIdEmail(@RequestParam("userName") String userName,
                                  @RequestParam("email") String email) {
        
        String fullId = authService.findIdByNameAndEmail(userName, email);
        
        if (fullId == null || fullId.trim().isEmpty()) {
            return "not_found";
        }

        boolean isSent = emailService.sendFullIdEmail(email, userName, fullId);
        
        return isSent ? "success" : "fail";
    }
}