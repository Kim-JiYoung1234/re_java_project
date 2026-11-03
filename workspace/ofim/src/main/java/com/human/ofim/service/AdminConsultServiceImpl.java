package com.human.ofim.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.human.ofim.dto.AdminConsultDto;
import com.human.ofim.mapper.AdminConsultMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminConsultServiceImpl implements AdminConsultService {

    private final AdminConsultMapper adminConsultMapper;

    @Override
    public List<AdminConsultDto> getConsultList(
            String consultType,
            Integer status,
            String delayStatus,
            String startDate,
            String endDate,
            String searchType,
            String keyword
    ) {
        return adminConsultMapper.selectConsultList(
                consultType, status, delayStatus, startDate, endDate, searchType, keyword
        );
    }

    @Override
    public int getTotalConsult() {
        return adminConsultMapper.countTotalConsult();
    }

    @Override
    public int getCompletedConsult() {
        return adminConsultMapper.countCompletedConsult();
    }

    @Override
    public int getWaitingConsult() {
        return adminConsultMapper.countWaitingConsult();
    }

    @Override
    public int getDelayConsult() {
        return adminConsultMapper.countDelayConsult();
    }

    @Override
    public int getTodayReceived() {
        return adminConsultMapper.countTodayReceived();
    }

    @Override
    public int getTodayComplete() {
        return adminConsultMapper.countTodayComplete();
    }

    @Override
    public int getCompletedRate() {
        int total = adminConsultMapper.countTotalConsult();
        if (total == 0) return 0;
        return (int) Math.round((adminConsultMapper.countCompletedConsult() * 100.0) / total);
    }

    @Override
    public int getDelayRate() {
        int total = adminConsultMapper.countTotalConsult();
        if (total == 0) return 0;
        return (int) Math.round((adminConsultMapper.countDelayConsult() * 100.0) / total);
    }

    @Transactional
    @Override
    public void saveAdminReply(AdminConsultDto dto) {
        Integer beforeStatus = adminConsultMapper.selectCurrentStatus(dto.getConsultId());

        adminConsultMapper.insertAdminReply(dto);
        adminConsultMapper.updateConsultStatus(dto.getConsultId(), 3);

        adminConsultMapper.insertConsultManageHistory(
                dto.getConsultId(),
                "관리자답변등록",
                null,
                null,
                beforeStatus,
                3,
                (dto.getReason() == null || dto.getReason().isBlank()) ? "관리자 답변 등록" : dto.getReason(),
                dto.getAdminId()
        );
    }

    @Transactional
    @Override
    public void updateConsult(AdminConsultDto dto) {
        Integer beforeStatus = adminConsultMapper.selectCurrentStatus(dto.getConsultId());
        Long beforeExpert = adminConsultMapper.selectCurrentExpertId(dto.getConsultId());

        if ("complete".equals(dto.getActionType())) {

            adminConsultMapper.updateConsultStatus(dto.getConsultId(), 3);

            adminConsultMapper.insertConsultManageHistory(
                    dto.getConsultId(),
                    "답변완료",
                    beforeExpert,
                    beforeExpert,
                    beforeStatus,
                    3,
                    dto.getReason(),
                    dto.getAdminId()
            );

        } else if ("changeExpert".equals(dto.getActionType())) {

            adminConsultMapper.updateConsultExpert(dto.getConsultId(), dto.getNewExpertId().longValue());

            adminConsultMapper.insertConsultManageHistory(
                    dto.getConsultId(),
                    "전문가변경",
                    beforeExpert,
                    dto.getNewExpertId().longValue(),
                    beforeStatus,
                    beforeStatus,
                    dto.getReason(),
                    dto.getAdminId()
            );

        } else if ("release".equals(dto.getActionType())) {

            adminConsultMapper.insertConsultManageHistory(
                    dto.getConsultId(),
                    "접수해제",
                    beforeExpert,
                    beforeExpert,
                    beforeStatus,
                    beforeStatus,
                    dto.getReason(),
                    dto.getAdminId()
            );
        }
    }
}