package com.human.ofim.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.human.ofim.dto.FitnessCenterDto;
import com.human.ofim.dto.PartnerUserDto;

@Mapper
public interface AdminFitnessMapper {
    // DB의 모든 파트너 조회로 변경 (HTML에서 승인/대기 분리)
    List<PartnerUserDto> selectAllPartnerList();
    
    void updatePartnerApproveStatus(@Param("memberId") Long memberId, @Param("approveStatus") Integer approveStatus);
    List<FitnessCenterDto> selectAdminCenterList(@Param("memberId") Long memberId, @Param("keyword") String keyword);
    void updateCenterApproveStatus(@Param("centerNo") Integer centerNo, @Param("approveStatus") Integer approveStatus);
    FitnessCenterDto selectCenterDetail(@Param("centerNo") Integer centerNo);
    void deleteCenter(@Param("centerNo") Integer centerNo);
}