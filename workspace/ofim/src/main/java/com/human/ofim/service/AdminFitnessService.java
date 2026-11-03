package com.human.ofim.service;

import java.util.List;
import com.human.ofim.dto.FitnessCenterDto;
import com.human.ofim.dto.PartnerUserDto;

public interface AdminFitnessService {
    List<PartnerUserDto> getAllPartnerList();
    void updatePartnerApproveStatus(Long memberId, Integer approveStatus);
    List<FitnessCenterDto> getCenterList(Long memberId, String keyword);
    void updateApproveStatus(Integer centerNo, Integer approveStatus);
    FitnessCenterDto getCenterDetail(Integer centerNo);
    void deleteCenter(Integer centerNo);
}