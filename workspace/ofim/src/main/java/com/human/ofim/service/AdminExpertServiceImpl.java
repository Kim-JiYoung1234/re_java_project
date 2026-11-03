package com.human.ofim.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.human.ofim.dto.AdminExpertClientDto;
import com.human.ofim.dto.AdminExpertConsultStatsDto;
import com.human.ofim.dto.AdminExpertReviewSummaryDto;
import com.human.ofim.dto.CertificateDto;
import com.human.ofim.dto.ConsultDto;
import com.human.ofim.dto.ExpertSettlementDto;
import com.human.ofim.dto.ExpertUserDto;
import com.human.ofim.mapper.AdminExpertMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminExpertServiceImpl implements AdminExpertService {

    private final AdminExpertMapper adminExpertMapper;

    @Override
    public List<ExpertUserDto> getExpertList(String keyword, Integer status, Integer minConsult, String sort) {
        return adminExpertMapper.selectExpertList(keyword, status, minConsult, sort);
    }

    @Override
    public AdminExpertReviewSummaryDto getExpertReviewSummary() {
        AdminExpertReviewSummaryDto summary = adminExpertMapper.selectExpertReviewSummary();

        if (summary == null) {
            summary = new AdminExpertReviewSummaryDto();
        }

        return summary;
    }

    @Override
    public List<ExpertUserDto> getExpertReviewList(Integer approveStatus,
                                                   String memberStatus,
                                                   String onlineYn,
                                                   String searchType,
                                                   String keyword,
                                                   int offset,
                                                   int size) {
        return adminExpertMapper.selectExpertReviewList(
                approveStatus, memberStatus, onlineYn, searchType, keyword, offset, size
        );
    }

    @Override
    public int getExpertReviewCount(Integer approveStatus,
                                    String memberStatus,
                                    String onlineYn,
                                    String searchType,
                                    String keyword) {
        return adminExpertMapper.countExpertReviewList(
                approveStatus, memberStatus, onlineYn, searchType, keyword
        );
    }

    @Override
    public ExpertUserDto getExpertDetail(Long memberId) {
        return adminExpertMapper.selectExpertDetail(memberId);
    }

    @Override
    public ExpertUserDto getExpertReviewDetail(Long memberId) {
        return adminExpertMapper.selectExpertReviewDetail(memberId);
    }

    @Override
    public ExpertUserDto getExpertJoinInfo(Long memberId) {
        return adminExpertMapper.selectExpertJoinInfo(memberId);
    }

    @Override
    @Transactional
    public void updateExpertApproveStatus(Long memberId, int status) {
        int updated = adminExpertMapper.updateExpertApproveStatus(memberId, status);
        if (updated == 0) {
            throw new IllegalArgumentException("전문가 승인 상태 변경에 실패했습니다.");
        }
    }

    @Override
    public List<CertificateDto> getCertList(Long memberId) {
        return adminExpertMapper.selectCertListByMemberId(memberId);
    }

    @Override
    public CertificateDto getCertById(Long certId) {
        return adminExpertMapper.selectCertById(certId);
    }

    @Override
    @Transactional
    public void updateCertVerifyStatus(Long certId, int status, String reason) {
        int updated = adminExpertMapper.updateCertVerifyStatus(certId, status, reason);
        if (updated == 0) {
            throw new IllegalArgumentException("서류 상태 변경에 실패했습니다.");
        }
    }

    @Override
    public AdminExpertConsultStatsDto getExpertConsultStats(Long memberId) {
        AdminExpertConsultStatsDto stats = adminExpertMapper.selectExpertConsultStats(memberId);

        if (stats == null) {
            stats = new AdminExpertConsultStatsDto();
            stats.setTotalConsultCount(0);
            stats.setTotalClientCount(0);
            stats.setTotalMinutes(0);
        }

        return stats;
    }

    @Override
    public List<ConsultDto> getExpertConsultList(Long memberId) {
        return adminExpertMapper.selectExpertConsultList(memberId);
    }

    @Override
    public List<AdminExpertClientDto> getExpertClientList(Long memberId) {
        return adminExpertMapper.selectExpertClientList(memberId);
    }

    @Override
    @Transactional
    public void updateExpertMemberStatus(Long memberId, String actionType, String reason) {
        String memberStatus;

        switch (actionType) {
            case "discipline_stop":
                memberStatus = "징계정지";
                break;
            case "accident_stop":
                memberStatus = "사고정지";
                break;
            case "discipline_withdraw":
                memberStatus = "징계탈퇴";
                break;
            case "accident_withdraw":
                memberStatus = "사고탈퇴";
                break;
            default:
                throw new IllegalArgumentException("잘못된 상태 변경 요청입니다.");
        }

        int updated = adminExpertMapper.updateExpertMemberStatus(memberId, memberStatus, reason);
        if (updated == 0) {
            throw new IllegalArgumentException("전문가 상태 변경에 실패했습니다.");
        }
    }
    
    @Override
    @Transactional
    public void updateExpertRejectReason(Long memberId, String reason) {
        adminExpertMapper.updateExpertRejectReason(memberId, reason);
    }
    
    @Override
    public List<Map<String, Object>> getExpertCareer(Long memberId) {
        return adminExpertMapper.selectExpertCareer(memberId);
    }

    @Override
    public List<Map<String, Object>> getExpertEducation(Long memberId) {
        return adminExpertMapper.selectExpertEducation(memberId);
    }
    
    @Override
    public Map<String, Object> getExpertSettlement(Long memberId) {
        return adminExpertMapper.selectExpertSettlement(memberId);
    }

    @Override
    public List<Map<String, Object>> getSettlementList(String month) {
        return adminExpertMapper.selectSettlementList(month);
    }
    
    @Override
    public List<ExpertSettlementDto> getSettlementDetail(Long expertId, String month) {
        return adminExpertMapper.selectSettlementDetail(expertId, month);
    }

    @Override
    public ExpertSettlementDto getSettlementHeader(Long expertId, String month) {
        return adminExpertMapper.selectSettlementHeader(expertId, month);
    }
    
    @Override
    public String getTotalCareerText(Long memberId) {
        List<Map<String, Object>> careerList = adminExpertMapper.selectExpertCareer(memberId);

        if (careerList == null || careerList.isEmpty()) {
            return "0년";
        }

        int totalMonths = 0;

        for (Map<String, Object> career : careerList) {
            int year = 0;
            int month = 0;

            Object yearObj = career.get("career_year");
            Object monthObj = career.get("career_month");

            if (yearObj != null) {
                year = Integer.parseInt(String.valueOf(yearObj));
            }

            if (monthObj != null) {
                month = Integer.parseInt(String.valueOf(monthObj));
            }

            totalMonths += (year * 12) + month;
        }

        int totalYear = totalMonths / 12;
        int remainMonth = totalMonths % 12;

        if (remainMonth == 0) {
            return totalYear + "년";
        }

        return totalYear + "년 " + remainMonth + "개월";
    }
    
    @Override
    public boolean isAllCertApproved(Long memberId) {

        List<CertificateDto> certList = adminExpertMapper.selectCertListByMemberId(memberId);

        if (certList == null || certList.isEmpty()) {
            return false; // 서류 없으면 승인 불가
        }

        for (CertificateDto cert : certList) {
            if (cert.getVerifyStatus() != 1) {
                return false;
            }
        }

        return true;
    }
    
}