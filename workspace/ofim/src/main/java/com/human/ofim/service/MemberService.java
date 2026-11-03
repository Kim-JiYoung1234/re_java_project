package com.human.ofim.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // jakarta 대신 spring꺼 추천

import com.human.ofim.dto.MemberDto;
import com.human.ofim.mapper.MemberMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor // Autowired 대신 이걸 쓰면 코드가 훨씬 깔끔해져 형!
public class MemberService {

    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;

    // 1. 프로필 이미지 업데이트
    @Transactional
    public void updateProfileImage(String loginId, String imageName) {
        memberMapper.updateProfileImage(imageName, loginId);
    }
    
    // 2. 단일 필드 수정 (닉네임, 연락처, 이메일)
    @Transactional
    public void updateMemberField(Long memberId, String fieldName, String fieldValue) {
        Map<String, Object> params = new HashMap<>();
        params.put("memberId", memberId);
        params.put("fieldName", fieldName);
        params.put("fieldValue", fieldValue);
        memberMapper.updateMemberField(params);
    }

    // 3. 주소 정보 수정
    @Transactional
    public void updateMemberAddress(MemberDto addressDto) {
        memberMapper.updateMemberAddress(addressDto);
    }
    
    // 4. 회원 탈퇴 (비밀번호 확인 포함)
    @Transactional
    public boolean withdraw(String loginId, String rawPassword) {
        MemberDto user = memberMapper.findByLoginId(loginId);
        if (user != null && passwordEncoder.matches(rawPassword, user.getPassword())) {
            memberMapper.updateMemberStatusWithdraw(user.getMemberId()); 
            return true;
        } 
        return false;
    }
    
    // 5. 관리자용 회원 상태 변경 (징계, 사고 등)
    @Transactional
    public void memberLeave(int memberId, String category, String reason) {
        switch(category) {
            case "자발적":
                memberMapper.updateMemberStatusWithdraw(memberId);
                break;
            case "징계":
                memberMapper.updateMemberStatusDiscipline(memberId, reason);
                break;
            case "사고":
                memberMapper.updateMemberStatusAccident(memberId);
                break;
        }
    }

    /* ===================== 🌟 포인트 및 랭킹 추가 로직 (형이 하고 싶어 한 거!) ===================== */

    // 6. 포인트 히스토리 조회 (최근 10개)
    public List<Map<String, Object>> getPointHistory(int memberId) {
        return memberMapper.getPointHistory(memberId);
    }

    // 7. 현재 총점 조회
    public int getTotalActivityPoint(int memberId) {
        // user 테이블에서 직접 가져오거나 로직 처리
        MemberDto user = memberMapper.findByMemberId(memberId);
        return (user != null) ? user.getActivityPoint() : 0;
    }
}