package com.human.ofim.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.human.ofim.dto.EmailAuthDto;

@Mapper
public interface EmailAuthMapper {
    
    // 1. 메일 발송 시 토큰 정보 저장
    void insertEmailAuth(EmailAuthDto emailAuthDto);
    
    // 2. 사용자가 링크 클릭 시 유효한 토큰인지 확인 (만료시간 안 지났고, 아직 안 쓴 토큰만)
    EmailAuthDto getValidToken(@Param("email") String email, @Param("token") String token, @Param("type") String type);
    
    // 3. 비밀번호 변경 완료 시 토큰을 '사용됨'으로 처리 (재사용 방지)
    void updateTokenUsed(@Param("authId") int authId);
}