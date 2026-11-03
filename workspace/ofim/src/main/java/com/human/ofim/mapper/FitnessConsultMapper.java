package com.human.ofim.mapper;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.human.ofim.dto.FitnessConsultDto;

@Mapper
public interface FitnessConsultMapper {
    
    void insertConsult(FitnessConsultDto dto);
    
    void upsertPhoneStats(@Param("centerNo") int centerNo);
    
    int selectTodayPhoneCopyCount(@Param("partnerId") int partnerId);
    
    int selectNewConsultCount(@Param("partnerId") int partnerId);
    
    List<FitnessConsultDto> selectConsultListByPartnerId(@Param("partnerId") int partnerId);

    void updateConsultStatus(@Param("consultNo") int consultNo, @Param("partnerId") int partnerId);

    List<Map<String, Object>> selectPhoneCopyTrend(@Param("partnerId") int partnerId);

    List<Map<String, Object>> selectConsultTrend(@Param("partnerId") int partnerId);

    // [신규] 상담 내역에 파트너 메모 저장
    void updateConsultMemo(@Param("consultNo") int consultNo, @Param("partnerId") int partnerId, @Param("partnerMemo") String partnerMemo);
}