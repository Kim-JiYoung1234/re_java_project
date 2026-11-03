package com.human.ofim.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.human.ofim.dto.CertificateDto;
import com.human.ofim.dto.EducationDto;
import com.human.ofim.dto.ExperienceDto;
import com.human.ofim.dto.ExpertSettlementDto;
import com.human.ofim.dto.ExpertUserDto;
import com.human.ofim.dto.MemberDto;

@Mapper
public interface ExpertUserMapper {

	void insertExpert(ExpertUserDto expertDto);
	void insertCertificate(CertificateDto certDto);
	void insertEducation(EducationDto eduDto);
	void insertExperience(ExperienceDto expDto);
	void insertSettlement(ExpertSettlementDto setDto);
		
	void updateCert(CertificateDto certDto);
	void updateExpertProfile(ExpertUserDto expertDto);
	void updateExp(ExperienceDto expDto);
	void updateEdu(EducationDto eduDto);
	void updateSettlement(ExpertSettlementDto dto);

	void deleteCertificate(@Param("certificationId") Long certId, @Param("memberId") Long memberId);
	void deleteExp(Long careerId);
	void deleteEdu(Long careerId);
	
	MemberDto selectMemberById(Long memberId);
	ExpertUserDto selectExpertById(Long memberId);
	List<CertificateDto> selectCertList(Long memberId);
	List<EducationDto> selectEduList(Long memberId);
	List<ExperienceDto> selectExpList(Long memberId);
	ExpertSettlementDto selectSettlement(Long memberId);	
	
	Integer selectApproveStatus(Long memberId);
	
	 int updateOnlineStatus(@Param("memberId") Long memberId,
			 				@Param("isOnline") boolean isOnline);

}
