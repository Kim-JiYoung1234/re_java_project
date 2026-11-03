package com.human.ofim.service;

import java.util.List;
import java.util.Map;

import org.jspecify.annotations.Nullable;

import com.human.ofim.dto.AdminExpertClientDto;
import com.human.ofim.dto.AdminExpertConsultStatsDto;
import com.human.ofim.dto.AdminExpertReviewSummaryDto;
import com.human.ofim.dto.CertificateDto;
import com.human.ofim.dto.ConsultDto;
import com.human.ofim.dto.ExpertSettlementDto;
import com.human.ofim.dto.ExpertUserDto;

public interface AdminExpertService {

    List<ExpertUserDto> getExpertList(String keyword, Integer status, Integer minConsult, String sort);

    AdminExpertReviewSummaryDto getExpertReviewSummary();
    
    List<ExpertUserDto> getExpertReviewList(Integer approveStatus,
								            String memberStatus,
								            String onlineYn,
								            String searchType,
								            String keyword,
								            int offset,
								            int size);
	
	int getExpertReviewCount(Integer approveStatus,
							String memberStatus,
							String onlineYn,
							String searchType,
							String keyword);

    ExpertUserDto getExpertDetail(Long memberId);
    
    ExpertUserDto getExpertReviewDetail(Long memberId);
    
    ExpertUserDto getExpertJoinInfo(Long memberId);

    void updateExpertApproveStatus(Long memberId, int status);

    List<CertificateDto> getCertList(Long memberId);

    CertificateDto getCertById(Long certId);

    void updateCertVerifyStatus(Long certId, int status, String reason);

    AdminExpertConsultStatsDto getExpertConsultStats(Long memberId);

    List<ConsultDto> getExpertConsultList(Long memberId);

    List<AdminExpertClientDto> getExpertClientList(Long memberId);
    
    void updateExpertRejectReason(Long memberId, String reason);  
    
    void updateExpertMemberStatus(Long memberId, String actionType, String reason);
    
    List<Map<String, Object>> getExpertCareer(Long memberId);

    List<Map<String, Object>> getExpertEducation(Long memberId);
    
    Map<String, Object> getExpertSettlement(Long memberId);

    List<Map<String, Object>> getSettlementList(String month);
    
    List<ExpertSettlementDto> getSettlementDetail(Long expertId, String month);
    ExpertSettlementDto getSettlementHeader(Long expertId, String month);
    
    boolean isAllCertApproved(Long memberId);
    
    String getTotalCareerText(Long memberId);
    
    

	
}