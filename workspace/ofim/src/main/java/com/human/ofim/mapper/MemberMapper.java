package com.human.ofim.mapper;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.human.ofim.dto.ExpertUserDto;
import com.human.ofim.dto.MemberDto;

@Mapper
public interface MemberMapper {

	void insertUser(MemberDto memberDto);
	void insertMember(long memberId);
	int countByLoginId(String loginId);
	int countByNickname(String nickname);
	MemberDto findByLoginId(String loginId);
	MemberDto selectMemberById(int memberId);
	
	String getPasswordById(@Param("loginId") String loginId);

	int countByEmail(String email);

	void updateLastLoginDate(String loginId);
	void updateMemberAddress(MemberDto addressDto);
	void updateMemberField(Map<String, Object> params);
	void updateExpertProfile(ExpertUserDto expertDto);
	void updatePassword(@Param("loginId") String loginId, @Param("encodedNewPassword") String encodedNewPassword);
	
	void updatePasswordByEmail(@Param("email") String email, @Param("encodedNewPassword") String encodedNewPassword);
	
	String findIdByNameAndEmail(@Param("userName") String userName, @Param("email") String email);

	int getTodayEarnedPoints(int memberId);
	void insertPointLog(@Param("memberId") int memberId, @Param("points") int points, @Param("reason") String reason);
	List<Map<String, Object>> getPointHistory(int memberId);
	void updateActivityPoint(@Param("points") int points, @Param("memberId") int memberId);
	List<MemberDto> selectTopRankers();

	int updateMemberStatusWithdraw(long memberId);
	int updateMemberStatusDiscipline(@Param("memberId") int memberId, @Param("reason") String reason);
	int updateMemberStatusAccident(long memberId);
	void updateProfileImage(@Param("profile") String profile, @Param("loginId") String loginId);
	Integer selectReplyWriterId(int replyId);
	MemberDto findByMemberId(int memberId);

	// 🌟 [보안 감사 로그 기초 메서드]
	void insertLoginAudit(@Param("loginId") String loginId, @Param("ipAddress") String ipAddress, @Param("userAgent") String userAgent, @Param("status") String status, @Param("failReason") String failReason);
	int countRecentFailedLogins(@Param("ipAddress") String ipAddress, @Param("loginId") String loginId);

    // 🌟 [보안 대시보드용 신규 메서드 추가]
    List<Map<String, Object>> selectRecentLoginAudit();
    Map<String, Object> selectTodayLoginStats();
}