package com.human.ofim.service;

import java.io.File;
import java.io.InputStream;
import java.util.UUID;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.human.ofim.dto.CertificateDto;
import com.human.ofim.dto.ExpertUserDto;
import com.human.ofim.dto.MemberDto;
import com.human.ofim.dto.PartnerUserDto;
import com.human.ofim.mapper.ExpertUserMapper;
import com.human.ofim.mapper.MemberMapper;
import com.human.ofim.mapper.PartnerUserMapper;
import com.human.ofim.config.SessionManager;

import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service("authService")
public class AuthService {

	@Autowired
	private MemberMapper memberMapper;
	@Autowired
	private PartnerUserMapper partnerUserMapper;
	@Autowired
	private ExpertUserMapper expertUserMapper;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private HttpServletRequest request;
	@Autowired
	private SessionManager sessionManager;
	
	private final JavaMailSender mailSender;

	@Value("${file.upload-dir}")
	private String uploadPath;

	private void validatePassword(String rawPassword) {
		String pwPattern = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*?_]).{8,20}$";
		if (!Pattern.matches(pwPattern, rawPassword)) {
			throw new IllegalArgumentException("비밀번호는 영문, 숫자, 특수문자를 포함하여 8~20자여야 합니다.");
		}
	}

	// 1️⃣ 일반 회원가입
	public void register(MemberDto memberDto) {
		// 🌟 암호화하기 전에 쌩얼 비밀번호 검사!
		validatePassword(memberDto.getPassword()); 

		String encodedPassword = passwordEncoder.encode(memberDto.getPassword());
		memberDto.setPassword(encodedPassword);
		memberMapper.insertUser(memberDto);
		memberMapper.insertMember(memberDto.getMemberId());
	}

	// 2️⃣ 파트너 회원가입
	@Transactional
	public void registerPartnerMember(MemberDto memberDto, PartnerUserDto partnerUserDto) {
		// 🌟 암호화하기 전에 검사!
		validatePassword(memberDto.getPassword());

		memberDto.setPassword(passwordEncoder.encode(memberDto.getPassword()));
		memberMapper.insertUser(memberDto);
		partnerUserDto.setMemberId(memberDto.getMemberId());
		try {
			if (partnerUserDto.getBusinessDocsFile() != null && !partnerUserDto.getBusinessDocsFile().isEmpty()) {
				String savedBusinessName = saveFile(partnerUserDto.getBusinessDocsFile(), "partner_docs/business");
				partnerUserDto.setBusinessDocsPath(savedBusinessName);
			}
			if (partnerUserDto.getMailOrderDocsFile() != null && !partnerUserDto.getMailOrderDocsFile().isEmpty()) {
				String savedMailOrderName = saveFile(partnerUserDto.getMailOrderDocsFile(), "partner_docs/mail_order");
				partnerUserDto.setMailOrderDocsPath(savedMailOrderName);
			}
		} catch (Exception e) {
			throw new RuntimeException("파트너 서류 파일 저장 중 오류가 발생했습니다.", e);
		}
		partnerUserMapper.insertPartner(partnerUserDto);
	}

	// 3️⃣ 전문가 회원가입
	@Transactional
	public void registerExpertMember(MemberDto memberDto, ExpertUserDto expertUserDto, CertificateDto certificateDto) {
		// 🌟 암호화하기 전에 검사!
		validatePassword(memberDto.getPassword());

		memberDto.setPassword(passwordEncoder.encode(memberDto.getPassword()));
		memberMapper.insertUser(memberDto);
		Long newMemberId = memberDto.getMemberId();
		expertUserDto.setMemberId(newMemberId);
		certificateDto.setMemberId(newMemberId);
		try {
			if (expertUserDto.getProfileImageFile() != null && !expertUserDto.getProfileImageFile().isEmpty()) {
				String savedProfile = saveFile(expertUserDto.getProfileImageFile(), "expert_docs/profiles");
				expertUserDto.setProfileImagePath(savedProfile);
			}
			if (certificateDto.getCertFile() != null && !certificateDto.getCertFile().isEmpty()) {
				String savedCert = saveFile(certificateDto.getCertFile(), "expert_docs/certs");
				certificateDto.setCertFilePath(savedCert);
			}
		} catch (Exception e) {
			throw new RuntimeException("전문가 프로필/자격증 저장 중 오류가 발생했습니다.", e);
		}
		expertUserMapper.insertExpert(expertUserDto);
		expertUserMapper.insertCertificate(certificateDto);
	}

	// 🌟 [보안] 파일 위변조(웹쉘, 악성코드) 방지 시그니처 검사 로직
	private void validateFileSecurity(MultipartFile file) throws Exception {
		String originalName = file.getOriginalFilename();
		if (originalName == null || originalName.isEmpty()) throw new SecurityException("파일명이 존재하지 않습니다.");
		
		String lowerName = originalName.toLowerCase();
		if (lowerName.endsWith(".jsp") || lowerName.endsWith(".php") || lowerName.endsWith(".exe") || lowerName.endsWith(".sh") || lowerName.endsWith(".bat")) {
			throw new SecurityException("업로드가 제한된 위험한 파일 형식입니다.");
		}

		// 파일의 첫 4바이트(Magic Number)를 읽어 실제 파일 형식 검사
		byte[] header = new byte[4];
		try (InputStream is = file.getInputStream()) {
			is.read(header, 0, 4);
		}
		
		StringBuilder sb = new StringBuilder();
		for (byte b : header) sb.append(String.format("%02x", b));
		String magicNumber = sb.toString().toUpperCase();

		boolean isSafe = false;
		if (magicNumber.startsWith("FFD8FF")) isSafe = true; // JPG, JPEG
		else if (magicNumber.startsWith("89504E47")) isSafe = true; // PNG
		else if (magicNumber.startsWith("25504446")) isSafe = true; // PDF
		else if (magicNumber.startsWith("47494638")) isSafe = true; // GIF

		if (!isSafe) {
			throw new SecurityException("위조된 파일이거나 허용되지 않은 포맷입니다. (이미지 및 PDF만 허용)");
		}
	}

	private String saveFile(MultipartFile file, String subDir) throws Exception {
		// 1. 파일 위변조 1차 방어!
		validateFileSecurity(file); 

		// 2. 🌟 [수정 포인트] 경로 찌그러짐 완벽 방지 (File 객체로 합치기)
		File baseDir = new File(uploadPath);
		File targetDir = new File(baseDir, subDir);
		
		if (!targetDir.exists()) {
			targetDir.mkdirs(); // 폴더가 없으면 알아서 예쁘게 생성!
		}

		// 3. 원본 파일에서 확장자(예: .png, .jpg)만 쏙 뽑아내기
		String originalName = file.getOriginalFilename();
		String extension = "";
		if (originalName != null && originalName.contains(".")) {
			extension = originalName.substring(originalName.lastIndexOf("."));
		}

		// 4. 🌟 [수정 포인트] 마이페이지 수정할 때랑 똑같이 'UUID + 확장자'로 통일! (한글 깨짐 완벽 방지)
		String uuid = UUID.randomUUID().toString();
		String savedName = uuid + extension; 

		// 5. 최종 목적지에 파일 저장
		File dest = new File(targetDir, savedName);
		file.transferTo(dest);

		// DB에 저장될 문자열 리턴 (예: /uploads/expert_docs/profiles/557417ec...png)
		return "/uploads/" + subDir + "/" + savedName; 
	}

	public boolean isIdDuplicate(String loginId) { return memberMapper.countByLoginId(loginId) > 0; }
	public boolean isNicknameDuplicate(String nickname) { return memberMapper.countByNickname(nickname) > 0; }
	public boolean isEmailDuplicate(String email) { return memberMapper.countByEmail(email) > 0; }

	private String getClientIp() {
		String ip = request.getHeader("X-Forwarded-For");
		if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) ip = request.getHeader("Proxy-Client-IP");
		if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) ip = request.getHeader("WL-Proxy-Client-IP");
		if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) ip = request.getRemoteAddr();
		return ip;
	}

	public MemberDto login(String loginId, String rawPassword) {
		String ip = getClientIp();
		String ua = request.getHeader("User-Agent");
		
		int failCount = memberMapper.countRecentFailedLogins(ip, loginId);
		if (failCount >= 5) {
			memberMapper.insertLoginAudit(loginId, ip, ua, "BLOCKED", "연속 로그인 실패로 차단됨");
			throw new IllegalStateException("5회 이상 로그인 실패로 인해 5분간 접속이 차단됩니다.");
		}

		MemberDto member = memberMapper.findByLoginId(loginId);
		if (member == null) {
			memberMapper.insertLoginAudit(loginId, ip, ua, "FAIL", "존재하지 않는 아이디");
			return null;
		}

		if (!passwordEncoder.matches(rawPassword, member.getPassword())) {
			memberMapper.insertLoginAudit(loginId, ip, ua, "FAIL", "비밀번호 불일치");
			return null;
		}

		String currentStatus = member.getMemberStatus();
		if ("자발적탈퇴".equals(currentStatus) || "징계탈퇴".equals(currentStatus) || "사고탈퇴".equals(currentStatus)) {
			memberMapper.insertLoginAudit(loginId, ip, ua, "FAIL", "탈퇴 회원 접속 시도");
			throw new IllegalStateException("탈퇴 처리된 계정입니다.");
		}
		
		if ("징계정지".equals(currentStatus) || "사고정지".equals(currentStatus)) {
			memberMapper.insertLoginAudit(loginId, ip, ua, "FAIL", "정지 회원 접속 시도");
			throw new IllegalStateException("이용이 정지된 계정입니다. 고객센터에 문의해주세요.");
		}

		if ("파트너회원".equals(member.getMemberType())) {
			Integer status = partnerUserMapper.selectApproveStatus(member.getMemberId());
			if (status == null || status == 0) {
				memberMapper.insertLoginAudit(loginId, ip, ua, "FAIL", "파트너 미승인");
				throw new IllegalStateException("파트너 가입 승인 대기 중인 계정입니다.");
			} else if (status == 2) {
				memberMapper.insertLoginAudit(loginId, ip, ua, "FAIL", "파트너 반려");
				throw new IllegalStateException("파트너 가입이 반려된 계정입니다.");
			}
		}

		if ("전문가회원".equals(member.getMemberType())) {
			Integer status = expertUserMapper.selectApproveStatus(member.getMemberId());
			if (status == null || status == 0) {
				memberMapper.insertLoginAudit(loginId, ip, ua, "FAIL", "전문가 미승인");
				throw new IllegalStateException("전문가 가입 승인 대기 중인 계정입니다.");
			} else if (status == 2) {
				memberMapper.insertLoginAudit(loginId, ip, ua, "FAIL", "전문가 반려");
				throw new IllegalStateException("전문가 가입이 반려된 계정입니다.");
			}
		}

		memberMapper.insertLoginAudit(loginId, ip, ua, "SUCCESS", "로그인 성공");
		memberMapper.updateLastLoginDate(loginId);
		sessionManager.registerSession(loginId, request.getSession());

		return member;
	}

	public boolean changePassword(String loginId, String currentPassword, String newPassword) {
		MemberDto member = memberMapper.findByLoginId(loginId);
		if (member == null) return false;
		if (!passwordEncoder.matches(currentPassword, member.getPassword())) return false;
		String encodedNewPassword = passwordEncoder.encode(newPassword);
		memberMapper.updatePassword(loginId, encodedNewPassword);
		return true;
	}
	
	public String changeUserPassword(String loginId, String currentPassword, String newPassword) {
	    
	    // 1️⃣ 백엔드 정규식 검사 (영문, 숫자, 특수문자 포함 8~20자)
	    // 화면에서 자바스크립트로 막았더라도, 서버에서 한 번 더 튕겨냅니다!
	    String pwPattern = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*?_]).{8,20}$";
	    if (!Pattern.matches(pwPattern, newPassword)) {
	        return "invalid_pattern"; // 정규식 위반!
	    }

	    // 2️⃣ DB에서 이 회원의 진짜(암호화된) 비밀번호를 가져옵니다.
	    String dbEncryptedPassword = memberMapper.getPasswordById(loginId); 
	    
	    if (dbEncryptedPassword == null) {
	        return "not_found_user"; // 회원이 없음
	    }

	    // 3️⃣ [검증 1] 입력한 '현재 비밀번호'가 맞는지 확인
	    // 주의: (사용자가 입력한 쌩얼 비번, DB에 있는 암호화된 비번) 순서로 넣어야 합니다!
	    if (!passwordEncoder.matches(currentPassword, dbEncryptedPassword)) {
	        return "wrong_current"; // 현재 비번 틀림!
	    }

	    // 4️⃣ [검증 2] 입력한 '새 비밀번호'가 기존 비번과 똑같은지 확인
	    // 현재 비번과 똑같은 걸 새 비번으로 쓰려고 하면 차단합니다.
	    if (passwordEncoder.matches(newPassword, dbEncryptedPassword)) {
	        return "same_as_old"; // 기존과 똑같음! 차단!
	    }

	    // 5️⃣ 모든 방어벽을 통과했다면? 새 비밀번호를 암호화해서 DB에 업데이트!
	    String encodedNewPassword = passwordEncoder.encode(newPassword);
	    memberMapper.updatePassword(loginId, encodedNewPassword); 

	    return "success";
	}
	
	public void updatePassword(String email, String newPassword) {
		validatePassword(newPassword);
		String encoded = passwordEncoder.encode(newPassword); 
		memberMapper.updatePasswordByEmail(email, encoded); 
	}
	
	public String findIdByNameAndEmail(String userName, String email) {
        // 매퍼를 통해 DB에서 login_id를 한 줄 가져옵니다.
        return memberMapper.findIdByNameAndEmail(userName, email);
    }

	
	
}