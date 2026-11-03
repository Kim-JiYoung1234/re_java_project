package com.human.ofim.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.human.ofim.dto.FitnessCenterDto;
import com.human.ofim.dto.PartnerUserDto;
import com.human.ofim.mapper.AdminFitnessMapper;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminFitnessServiceImpl implements AdminFitnessService {

    private final AdminFitnessMapper adminFitnessMapper;

    @Override
    public List<PartnerUserDto> getAllPartnerList() {
        return adminFitnessMapper.selectAllPartnerList();
    }

    @Override
    public void updatePartnerApproveStatus(Long memberId, Integer approveStatus) {
        adminFitnessMapper.updatePartnerApproveStatus(memberId, approveStatus);
    }

    @Override
    public List<FitnessCenterDto> getCenterList(Long memberId, String keyword) {
        return adminFitnessMapper.selectAdminCenterList(memberId, keyword);
    }

    @Override
    public void updateApproveStatus(Integer centerNo, Integer approveStatus) {
        adminFitnessMapper.updateCenterApproveStatus(centerNo, approveStatus);
    }

    @Override
    public FitnessCenterDto getCenterDetail(Integer centerNo) {
        return adminFitnessMapper.selectCenterDetail(centerNo);
    }

    @Override
    public void deleteCenter(Integer centerNo) {
        adminFitnessMapper.deleteCenter(centerNo);
    }
}