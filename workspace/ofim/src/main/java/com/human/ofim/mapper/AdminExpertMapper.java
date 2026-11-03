package com.human.ofim.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.human.ofim.dto.AdminExpertClientDto;
import com.human.ofim.dto.AdminExpertConsultStatsDto;
import com.human.ofim.dto.AdminExpertReviewSummaryDto;
import com.human.ofim.dto.CertificateDto;
import com.human.ofim.dto.ConsultDto;
import com.human.ofim.dto.ExpertSettlementDto;
import com.human.ofim.dto.ExpertUserDto;

@Mapper
public interface AdminExpertMapper {

    List<ExpertUserDto> selectExpertList(@Param("keyword") String keyword,
                                         @Param("status") Integer status,
                                         @Param("minConsult") Integer minConsult,
                                         @Param("sort") String sort);

    AdminExpertReviewSummaryDto selectExpertReviewSummary();
    
    List<ExpertUserDto> selectExpertReviewList(@Param("approveStatus") Integer approveStatus,
            @Param("memberStatus") String memberStatus,
            @Param("onlineYn") String onlineYn,
            @Param("searchType") String searchType,
            @Param("keyword") String keyword,
            @Param("offset") int offset,
            @Param("size") int size);
    
    int countExpertReviewList(@Param("approveStatus") Integer approveStatus,
            @Param("memberStatus") String memberStatus,
            @Param("onlineYn") String onlineYn,
            @Param("searchType") String searchType,
            @Param("keyword") String keyword);
    
    ExpertUserDto selectExpertDetail(@Param("memberId") Long memberId);
    
    ExpertUserDto selectExpertReviewDetail(@Param("memberId") Long memberId);
    
    ExpertUserDto selectExpertJoinInfo(@Param("memberId") Long memberId);

    int updateExpertApproveStatus(@Param("memberId") Long memberId,
                                  @Param("status") int status);

    List<CertificateDto> selectCertListByMemberId(@Param("memberId") Long memberId);

    CertificateDto selectCertById(@Param("certId") Long certId);

    int updateCertVerifyStatus(@Param("certId") Long certId,
                               @Param("status") int status,
                               @Param("reason") String reason);

    AdminExpertConsultStatsDto selectExpertConsultStats(@Param("memberId") Long memberId);

    List<ConsultDto> selectExpertConsultList(@Param("memberId") Long memberId);

    List<AdminExpertClientDto> selectExpertClientList(@Param("memberId") Long memberId);

    int updateExpertMemberStatus(@Param("memberId") Long memberId,
                                 @Param("actionType") String actionType);

    int updateExpertMemberStatus(@Param("memberId") Long memberId,
						         @Param("memberStatus") String memberStatus,
						         @Param("reason") String reason);
    
    int updateExpertRejectReason(@Param("memberId") Long memberId,
            					 @Param("reason") String reason);
    
    List<Map<String, Object>> selectExpertCareer(@Param("memberId") Long memberId);

    List<Map<String, Object>> selectExpertEducation(@Param("memberId") Long memberId);
    
    Map<String, Object> selectExpertSettlement(@Param("memberId") Long memberId);

    List<Map<String, Object>> selectSettlementList(@Param("month") String month);
    
    List<ExpertSettlementDto> selectSettlementDetail(@Param("expertId") Long expertId,
            @Param("month") String month);
    
    ExpertSettlementDto selectSettlementHeader(@Param("expertId") Long expertId,
            @Param("month") String month);
}