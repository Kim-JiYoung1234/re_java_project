package com.human.ofim.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.human.ofim.dto.ConsultChatMessageDto;
import com.human.ofim.dto.ConsultDto;
import com.human.ofim.dto.InquiryDto;

@Mapper
public interface ConsultMapper {

    List<ConsultDto> selectExpertList(
            @Param("keyword") String keyword,
            @Param("specialty") String specialty,
            @Param("sort") String sort,
            @Param("onlineOnly") Boolean onlineOnly,
            @Param("size") int size,
            @Param("offset") int offset
    );

    long countExperts(
            @Param("keyword") String keyword,
            @Param("specialty") String specialty,
            @Param("onlineOnly") Boolean onlineOnly
    );

    List<String> selectSpecialtyOptions();

    ConsultDto selectExpertDetail(@Param("expertId") Long expertId);

    List<Map<String, Object>> selectCareerList(@Param("expertId") Long expertId);

    List<Map<String, Object>> selectEducationList(@Param("expertId") Long expertId);

    List<Map<String, Object>> selectCertificateList(@Param("expertId") Long expertId);

    Map<String, Object> selectReviewSummary(@Param("expertId") Long expertId);

    Map<String, Object> selectReserveExpertInfo(@Param("expertId") Long expertId);

    void insertReservation(ConsultDto dto);

    void updateReservation(ConsultDto dto);

    Map<String, Object> selectReservationDetailForEdit(
            @Param("consultId") Long consultId,
            @Param("memberId") Long memberId
    );

    void updateConsultStatus(@Param("consultId") Long consultId,
                             @Param("status") Integer status);

    List<Map<String, Object>> selectMyReservations(@Param("memberId") Long memberId);

    List<Map<String, Object>> selectConsultsForExpert(@Param("expertId") Long expertId);

    int cancelReservation(
            @Param("consultId") Long consultId,
            @Param("memberId") Long memberId
    );

    void autoCompleteConsultation();

    List<String> selectAvailableTimeSlots(
            @Param("expertId") Long expertId,
            @Param("date") String date,
            @Param("consultType") String consultType
    );

    void insertInquiryConsultation(ConsultDto dto);

    void insertInquiry(InquiryDto dto);
    
    List<InquiryDto> getInquiryList(Long consultId);
    
    void insertCallNote(InquiryDto dto);

	InquiryDto selectCallNoteByConsultId(@Param("consultId") Long consultId);
	
	int countCallNoteByConsultId(@Param("consultId") Long consultId);
	void updateCallNote(InquiryDto dto);
        
    List<Map<String, Object>> getConsultListByMemberId(Long memberId);

    Map<String, Object> selectConsultByIdAndMember(@Param("consultId") Long consultId,
										            @Param("memberId") Long memberId
	);

    List<Map<String, Object>> selectConsultHistory(@Param("memberId") Long memberId,
										           @Param("expertName") String expertName,
										           @Param("consultType") String consultType,
										           @Param("status") Integer status,
										           @Param("dateFrom") String dateFrom,
										           @Param("dateTo") String dateTo
    );

	List<ConsultChatMessageDto> selectChatMessagesByConsultId(Long consultId);
	
	
    
}