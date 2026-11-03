package com.human.ofim.controller;

import java.io.File; // 🌟 올바른 파일 클래스 임포트
import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.human.ofim.dto.MemberDto;
import com.human.ofim.service.AuthService;
import com.human.ofim.service.MemberService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/member")
public class MemberController {

	@Autowired
	private MemberService memberService;
	@Autowired
    private AuthService authService;

	// 프로필 이미지 업로드 처리
	@PostMapping("/uploadProfile")
	public String uploadProfile(@RequestParam("profileFile") MultipartFile file, HttpSession session,
			RedirectAttributes rttr) {

		// 1. 세션에서 현재 로그인한 유저 정보 가져오기
		MemberDto loginUser = (MemberDto) session.getAttribute("user");

		// 세션 만료 시 로그인 페이지로 유도 (방어 코드)
		if (loginUser == null) {
			rttr.addFlashAttribute("msg", "로그인이 필요합니다.");
			return "redirect:/auth/login";
		}

		if (file.isEmpty()) {
			rttr.addFlashAttribute("msg", "파일이 선택되지 않았습니다.");
			return "redirect:/mypage/main";
		}

		try {
			// 2. 파일 저장 경로 설정 (실제 서버 내 폴더)
			String uploadPath = "D:\\ofim_uploads";
			File folder = new File(uploadPath);
			if (!folder.exists()) {
				folder.mkdirs(); // 폴더 없으면 생성
			}

			// 3. 파일명 중복 방지 (UUID 사용)
			String originalName = file.getOriginalFilename();
			String uuid = UUID.randomUUID().toString();
			String saveName = uuid + "_" + originalName;

			// 4. 서버 폴더에 실제 파일 저장
			File saveFile = new File(uploadPath, saveName);
			file.transferTo(saveFile);

			// 5. DB 업데이트 (Service 호출)
			memberService.updateProfileImage(loginUser.getLoginId(), saveName);

			// 6. 🌟 세션 정보 갱신 (화면에 바로 반영하기 위함)
			loginUser.setProfile(saveName);
			session.setAttribute("user", loginUser);

			rttr.addFlashAttribute("msg", "프로필 사진이 변경되었습니다.");

		} catch (IOException e) {
			e.printStackTrace();
			rttr.addFlashAttribute("msg", "업로드 중 오류가 발생했습니다.");
		}

		return "redirect:/mypage/main";
	}
	
	@GetMapping("/user/check-duplicate")
    @ResponseBody
    public boolean checkDuplicate(@RequestParam("field") String field, @RequestParam("value") String value) {
        
        boolean isDuplicate = false;
        
        // 어떤 항목을 검사할지에 따라 Service의 메서드를 다르게 호출합니다.
        switch (field) {
            case "nickname":
                isDuplicate = authService.isNicknameDuplicate(value);
                break;
            case "email":
                isDuplicate = authService.isEmailDuplicate(value);
                break;
            default:
                break;
        }
        
        // 중복이면 true, 사용 가능하면 false를 반환합니다.
        return isDuplicate;
    }
	
	@PostMapping("/change-password")
	@ResponseBody
	public String changePassword(@RequestParam("currentPassword") String currentPassword,
	                             @RequestParam("newPassword") String newPassword,
	                             HttpSession session) {

	    MemberDto loginUser = (MemberDto) session.getAttribute("user");
	    if (loginUser == null) {
	        return "not_logged_in";
	    }

	    // 🌟 [핵심 수정] boolean 대신 String을 반환하는 새로운 로직 호출!
        // (이전에 설명해드린 AuthService의 changeUserPassword 메서드를 호출하도록 바꿉니다)
	    String result = authService.changeUserPassword(loginUser.getLoginId(), currentPassword, newPassword);

	    // 결과값("success", "wrong_current", "same_as_old" 등)이 그대로 프론트엔드로 날아감!
	    return result; 
	}
	
	@PostMapping("/withdraw")
	@ResponseBody 
	public String withdraw(@RequestParam("password") String password, HttpSession session) {
	    
	    // 1. 현재 로그인한 사용자 정보 가져오기
	    MemberDto loginUser = (MemberDto) session.getAttribute("user");
	    
	    if (loginUser == null) {
	        return "error"; // 세션 만료 등의 이유로 정보가 없을 때
	    }

	    // 2. Service 호출: 비밀번호 검증 + 아이디 변조 + 상태값 변경(update) 통합 처리
	    // 이제 내부적으로 memberMapper.updateMemberStatusWithdraw()를 실행하게 됩니다.
	    boolean isProcessed = memberService.withdraw(loginUser.getLoginId(), password);

	    // 3. 결과에 따른 처리
	    if (isProcessed) {
	        // ✅ 탈퇴(업데이트) 성공 시: 
	        // 1) 세션을 무효화하여 즉시 로그아웃 처리
	        session.invalidate(); 
	        // 2) 프론트엔드(AJAX)에 성공 신호 보냄
	        return "success";
	    } else {
	        // ❌ 탈퇴 실패: 비밀번호가 틀렸거나 유저 정보를 찾을 수 없을 때
	        return "wrong_password";
	    }
	}
}