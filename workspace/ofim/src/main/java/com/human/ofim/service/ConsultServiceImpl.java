package com.human.ofim.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.human.ofim.dto.ConsultChatMessageDto;
import com.human.ofim.dto.ConsultDto;
import com.human.ofim.dto.InquiryDto;
import com.human.ofim.mapper.ConsultMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConsultServiceImpl implements ConsultService {

    private final ConsultMapper consultMapper;
    private final MembershipService membershipService;

    @Override
    public List<ConsultDto> getExpertList(String keyword, String specialty, String sort, Boolean onlineOnly, int page, int size) {
        int offset = (page - 1) * size;
        return consultMapper.selectExpertList(keyword, specialty, sort, onlineOnly, size, offset);
    }

    @Override
    public long getTotalCount(String keyword, String specialty, Boolean onlineOnly) {
        return consultMapper.countExperts(keyword, specialty, onlineOnly);
    }

    @Override
    public List<String> getSpecialtyOptions() {
        return consultMapper.selectSpecialtyOptions();
    }

    @Override
    public ConsultDto getExpertDetail(Long expertId) {
        return consultMapper.selectExpertDetail(expertId);
    }

    @Override
    public List<Map<String, Object>> getCareerList(Long expertId) {
        return consultMapper.selectCareerList(expertId);
    }

    @Override
    public List<Map<String, Object>> getEducationList(Long expertId) {
        return consultMapper.selectEducationList(expertId);
    }

    @Override
    public List<Map<String, Object>> getCertificateList(Long expertId) {
        return consultMapper.selectCertificateList(expertId);
    }

    @Override
    public Map<String, Object> getReviewSummary(Long expertId) {
        return consultMapper.selectReviewSummary(expertId);
    }

    @Override
    public Map<String, Object> getReserveExpertInfo(Long expertId) {
        return consultMapper.selectReserveExpertInfo(expertId);
    }

    @Override
    @Transactional
    public void reserveConsult(
            Long memberId,
            Long expertId,
            String consultType,
            String reserveDate,
            String reserveTime,
            String consultContent,
            String contactPhone
    ) {
        LocalDateTime startDateTime = LocalDateTime.parse(
                reserveDate + " " + reserveTime,
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        );

        ConsultDto dto = new ConsultDto();
        dto.setMemberId(memberId);
        dto.setExpertId(expertId);
        dto.setConsultType(consultType);
        dto.setConsultStartDate(startDateTime);
        dto.setConsultEndDate(startDateTime.plusHours(1));
        dto.setConsultContent(consultContent);
        dto.setContactPhone(contactPhone);
        dto.setStatus(1);

        consultMapper.insertReservation(dto);
    }

    @Override
    @Transactional
    public void updateReservation(
            Long consultId,
            Long memberId,
            Long expertId,
            String consultType,
            String reserveDate,
            String reserveTime,
            String consultContent,
            String contactPhone
    ) {
        LocalDateTime startDateTime = LocalDateTime.parse(
                reserveDate + " " + reserveTime,
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        );

        ConsultDto dto = new ConsultDto();
        dto.setConsultId(consultId);
        dto.setMemberId(memberId);
        dto.setExpertId(expertId);
        dto.setConsultType(consultType);
        dto.setConsultStartDate(startDateTime);
        dto.setConsultEndDate(startDateTime.plusHours(1));
        dto.setConsultContent(consultContent);
        dto.setContactPhone(contactPhone);

        consultMapper.updateReservation(dto);
    }

    @Override
    public Map<String, Object> getReservationDetailForEdit(Long consultId, Long memberId) {
        return consultMapper.selectReservationDetailForEdit(consultId, memberId);
    }

    @Override
    public List<Map<String, Object>> getMyReservations(Long memberId) {
        return consultMapper.selectMyReservations(memberId);
    }

    @Override
    public List<Map<String, Object>> getConsultsForExpert(Long expertId) {
        return consultMapper.selectConsultsForExpert(expertId);
    }

    @Override
    @Transactional
    public void cancelReservation(Long consultId, Long memberId) {
        int updated = consultMapper.cancelReservation(consultId, memberId);
        if (updated == 0) {
            throw new IllegalArgumentException("취소할 예약이 없거나 권한이 없습니다.");
        }
    }

    @Override
    @Transactional
    public void autoCompleteIfNeeded() {
        consultMapper.autoCompleteConsultation();
    }

    @Override
    public List<String> getAvailableTimeSlots(Long expertId, String date, String consultType) {
        return consultMapper.selectAvailableTimeSlots(expertId, date, consultType);
    }

    @Override
    @Transactional
    public void insertInquiry(
            Long memberId,
            Long expertId,
            String consultType,
            String title,
            String content,
            MultipartFile[] files
    ) {
        membershipService.validateInquiryAvailable(memberId);

        ConsultDto consultDto = new ConsultDto();
        consultDto.setMemberId(memberId);
        consultDto.setExpertId(expertId);
        consultDto.setConsultType(consultType);
        consultDto.setStatus(1);

        LocalDateTime now = LocalDateTime.now();
        consultDto.setConsultStartDate(now);
        consultDto.setConsultEndDate(now);

        consultMapper.insertInquiryConsultation(consultDto);

        InquiryDto inquiryDto = new InquiryDto();
        inquiryDto.setConsultId(consultDto.getConsultId());
        inquiryDto.setContent("[제목] " + title + "\n\n" + content);
        inquiryDto.setCommentType("질문");

        consultMapper.insertInquiry(inquiryDto);

        membershipService.useInquiry(memberId);

        // 첨부파일은 별도 attachment 테이블 도입 시 처리
    }

    @Override
    @Transactional
    public void updateConsultStatus(Long consultId, Integer status) {
        consultMapper.updateConsultStatus(consultId, status);
    }
    
    @Override // 인터페이스의 약속을 지키겠다는 뜻
    @Transactional // 둘 중 하나라도 에러 나면 롤백!
    public boolean submitExpertAnswer(Long consultId, String answer) {
        try {
            InquiryDto dto = new InquiryDto();
            dto.setConsultId(consultId);
            dto.setContent(answer);
            dto.setCommentType("전문가답변");

            consultMapper.insertInquiry(dto);
            consultMapper.updateConsultStatus(consultId, 3);

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public List<Map<String, Object>> getConsultList(Long memberId) {
        return consultMapper.getConsultListByMemberId(memberId);
    }
    @Override
    public List<Map<String, Object>> getConsultHistory(
            Long memberId,
            String expertName,
            String consultType,
            Integer status,
            String dateFrom,
            String dateTo
    ) {
        return consultMapper.selectConsultHistory(
                memberId,
                expertName,
                consultType,
                status,
                dateFrom,
                dateTo
        );
    }

    @Override
    public Map<String, Object> getConsultByIdAndMember(Long consultId, Long memberId) {
        return consultMapper.selectConsultByIdAndMember(consultId, memberId);
    }

    @Override
    public List<ConsultChatMessageDto> getChatMessagesByConsultId(Long consultId) {
        return consultMapper.selectChatMessagesByConsultId(consultId);
    }
    
    @Override
    @Transactional
    public void saveCallNote(Long consultId, Long expertId, String content, MultipartFile file) {
        try {
            String originalName = null;
            String storedName = null;
            String filePath = null;

            if (file != null && !file.isEmpty()) {
                originalName = file.getOriginalFilename();

                String ext = "";
                int dotIndex = originalName.lastIndexOf(".");
                if (dotIndex > -1) {
                    ext = originalName.substring(dotIndex);
                }

                storedName = System.currentTimeMillis() + "_" + java.util.UUID.randomUUID() + ext;

                String uploadDir = "D:/ofim_uploads/call/";
                java.io.File dir = new java.io.File(uploadDir);
                if (!dir.exists()) dir.mkdirs();

                java.io.File saveFile = new java.io.File(uploadDir + storedName);
                file.transferTo(saveFile);

                filePath = "/uploads/call/" + storedName;
            }

            InquiryDto dto = new InquiryDto();
            dto.setConsultId(consultId);
            dto.setContent(content);
            dto.setCommentType("전화상담기록");
            dto.setFileOriginalName(originalName);
            dto.setFileStoredName(storedName);
            dto.setFilePath(filePath);

            int exists = consultMapper.countCallNoteByConsultId(consultId);

            if (exists > 0) {
                consultMapper.updateCallNote(dto);
            } else {
                consultMapper.insertCallNote(dto);
            }

        } catch (Exception e) {
            throw new RuntimeException("전화상담 기록 저장 실패", e);
        }
    }

    @Override
    public InquiryDto getCallNote(Long consultId) {
        return consultMapper.selectCallNoteByConsultId(consultId);
    }
}