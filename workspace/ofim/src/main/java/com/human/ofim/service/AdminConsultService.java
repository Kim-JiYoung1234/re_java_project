package com.human.ofim.service;

import java.util.List;

import com.human.ofim.dto.AdminConsultDto;

public interface AdminConsultService {

    List<AdminConsultDto> getConsultList(
            String consultType,
            Integer status,
            String delayStatus,
            String startDate,
            String endDate,
            String searchType,
            String keyword
    );

    int getTotalConsult();
    int getCompletedConsult();
    int getWaitingConsult();
    int getDelayConsult();
    int getTodayReceived();
    int getTodayComplete();

    int getCompletedRate();
    int getDelayRate();

    void saveAdminReply(AdminConsultDto dto);

    void updateConsult(AdminConsultDto dto);
}