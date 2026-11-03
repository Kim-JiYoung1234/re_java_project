package com.human.ofim.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.human.ofim.dto.EmailAuthDto;
import com.human.ofim.mapper.EmailAuthMapper;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

    // application.properties에 설정한 메일 정보를 바탕으로 작동하는 스프링 내장 객체
    private final JavaMailSender mailSender;
    private final EmailAuthMapper emailAuthMapper;
    
    @Transactional
    public boolean sendFullIdEmail(String email, String userName, String fullId) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("[OFIM] " + userName + "님의 전체 아이디 안내입니다.");
            
            String htmlMsg = "<div style='font-family: sans-serif; padding: 30px; border-radius: 10px; background-color: #f8fafc;'>"
                           + "<h2 style='color: #0d6efd;'>OUT FAT IN MUSCLE</h2>"
                           + "<h3>안녕하세요, " + userName + "님!</h3>"
                           + "<p>요청하신 전체 아이디는 <strong style='color: #e53e3e; font-size: 18px;'>" + fullId + "</strong> 입니다.</p>"
                           + "<p>로그인 후 서비스를 정상적으로 이용해 주세요.</p>"
                           + "</div>";
                           
            helper.setText(htmlMsg, true);
            mailSender.send(message);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Transactional
    public void sendPasswordResetEmail(String email) {
        // 1. 고유한 랜덤 토큰(UUID) 생성
        String token = UUID.randomUUID().toString();

        // 2. DB에 토큰 정보 저장 (30분 후 만료)
        EmailAuthDto authDto = new EmailAuthDto();
        authDto.setEmail(email);
        authDto.setToken(token);
        authDto.setType("PW_RESET");
        authDto.setExpiredAt(LocalDateTime.now().plusMinutes(30)); 
        
        emailAuthMapper.insertEmailAuth(authDto);

        // 3. 이메일 발송 준비
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email); // 받는 사람
            helper.setSubject("[OFIM] 비밀번호 재설정 안내 메일입니다."); // 메일 제목

            // 🌟 비밀번호 재설정 페이지로 이동할 링크 (도메인은 개발 환경에 맞게 수정)
            String resetLink = "http://localhost:8080/auth/reset-password?token=" + token + "&email=" + email;

            // 메일 내용 (HTML 형식 적용)
            String htmlContent = "<div style='margin: 50px; padding: 40px; border: 1px solid #f1f5f9; border-radius: 20px; text-align: center; font-family: sans-serif;'>"
                    + "<h2 style='color: #334155; margin-bottom: 20px;'>비밀번호 재설정 안내</h2>"
                    + "<p style='color: #64748b; margin-bottom: 30px;'>아래 버튼을 클릭하여 새로운 비밀번호를 설정해 주세요.<br>이 링크는 <strong>30분 동안만 유효</strong>합니다.</p>"
                    + "<a href='" + resetLink + "' style='background-color: #3b82f6; color: white; padding: 12px 30px; text-decoration: none; border-radius: 8px; font-weight: bold; display: inline-block;'>비밀번호 재설정하기</a>"
                    + "</div>";

            helper.setText(htmlContent, true); // true를 주면 HTML 코드로 인식함

            // 4. 진짜로 메일 쏘기!
            mailSender.send(message);

        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("이메일 발송 중 오류가 발생했습니다.");
        }
    }
}