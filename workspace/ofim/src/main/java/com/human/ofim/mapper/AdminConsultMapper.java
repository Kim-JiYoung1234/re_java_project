package com.human.ofim.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.human.ofim.dto.AdminConsultDto;

@Mapper
public interface AdminConsultMapper {

    List<AdminConsultDto> selectConsultList(
            @Param("consultType") String consultType,
            @Param("status") Integer status,
            @Param("delayStatus") String delayStatus,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("searchType") String searchType,
            @Param("keyword") String keyword
    );

    int countTotalConsult();
    int countCompletedConsult();
    int countWaitingConsult();
    int countDelayConsult();
    int countTodayReceived();
    int countTodayComplete();

    void insertAdminReply(AdminConsultDto dto);

    void updateConsultStatus(@Param("consultId") Long consultId,
                             @Param("status") int status);

    void updateConsultExpert(@Param("consultId") Long consultId,
                             @Param("expertId") Long expertId);

    Integer selectCurrentStatus(@Param("consultId") Long consultId);

    Long selectCurrentExpertId(@Param("consultId") Long consultId);

    void insertConsultManageHistory(@Param("consultId") Long consultId,
                                    @Param("actionType") String actionType,
                                    @Param("beforeExpertId") Long beforeExpertId,
                                    @Param("afterExpertId") Long afterExpertId,
                                    @Param("beforeStatus") Integer beforeStatus,
                                    @Param("afterStatus") Integer afterStatus,
                                    @Param("reason") String reason,
                                    @Param("adminId") Integer adminId);
}