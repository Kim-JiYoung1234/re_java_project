package com.human.ofim.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.human.ofim.dto.ConsultChatMessageDto;
import com.human.ofim.dto.ConsultDto;
import com.human.ofim.dto.InquiryDto;

public interface ConsultService {

    List<ConsultDto> getExpertList(String keyword, String specialty, String sort, Boolean onlineOnly, int page, int size);

    long getTotalCount(String keyword, String specialty, Boolean onlineOnly);

    List<String> getSpecialtyOptions();

    ConsultDto getExpertDetail(Long expertId);

    List<Map<String, Object>> getCareerList(Long expertId);

    List<Map<String, Object>> getEducationList(Long expertId);

    List<Map<String, Object>> getCertificateList(Long expertId);

    Map<String, Object> getReviewSummary(Long expertId);

    Map<String, Object> getReserveExpertInfo(Long expertId);

    void reserveConsult(
            Long memberId,
            Long expertId,
            String consultType,
            String reserveDate,
            String reserveTime,
            String consultContent,
            String contactPhone
    );

    void updateReservation(
            Long consultId,
            Long memberId,
            Long expertId,
            String consultType,
            String reserveDate,
            String reserveTime,
            String consultContent,
            String contactPhone
    );

    Map<String, Object> getReservationDetailForEdit(Long consultId, Long memberId);

    List<Map<String, Object>> getMyReservations(Long memberId);

    List<Map<String, Object>> getConsultsForExpert(Long expertId);

    void cancelReservation(Long consultId, Long memberId);

    void autoCompleteIfNeeded();

    List<String> getAvailableTimeSlots(Long expertId, String date, String consultType);

    void updateConsultStatus(Long consultId, Integer status);

    void insertInquiry(
            Long memberId,
            Long expertId,
            String consultType,
            String title,
            String content,
            MultipartFile[] files
    );
    
    boolean submitExpertAnswer(Long consultId, String answer);
    
    List<Map<String, Object>> getConsultList(Long memberId);

	List<Map<String, Object>> getConsultHistory(Long memberId, String expertName, 
												String consultType, Integer status,
												String dateFrom, String dateTo);

	Map<String, Object> getConsultByIdAndMember(Long consultId, Long memberId);

	List<ConsultChatMessageDto> getChatMessagesByConsultId(Long consultId);
	
	void saveCallNote(Long consultId, Long expertId, String content, MultipartFile file);

	InquiryDto getCallNote(Long consultId);

}