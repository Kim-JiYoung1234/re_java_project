package com.human.ofim.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.human.ofim.dto.ConsultChatAlertDto;
import com.human.ofim.dto.ConsultChatDto;
import com.human.ofim.dto.ConsultChatMessageDto;
import com.human.ofim.dto.ConsultChatRoomDto;
import com.human.ofim.dto.ConsultDto;
import com.human.ofim.dto.ConsultPendingDto;

@Mapper
public interface ConsultChatMapper {

    Integer selectActiveChatConsult(@Param("memberId") Long memberId,
                                    @Param("expertId") Long expertId);

    int insertConsult(ConsultDto dto);

    int countConsultParticipant(@Param("consultId") int consultId,
                                @Param("memberId") Long memberId);

    Integer selectConsultStatus(@Param("consultId") int consultId);

    int insertMessage(ConsultChatMessageDto dto);

    List<ConsultChatMessageDto> selectMessagesByConsultId(@Param("consultId") int consultId);

    int updateReadByConsultId(@Param("consultId") int consultId,
                              @Param("readerId") Long readerId);

    int countUnreadByConsultId(@Param("consultId") int consultId,
                               @Param("memberId") Long memberId);

    ConsultChatRoomDto selectChatRoomSummary(@Param("consultId") int consultId,
                                             @Param("memberId") Long memberId);

    ConsultChatAlertDto selectLatestPendingChatForExpert(@Param("expertId") Long expertId);

    int updateStatus(@Param("consultId") int consultId,
                     @Param("status") int status);

    ConsultChatDto selectConsultDetail(@Param("consultId") int consultId);

    ConsultPendingDto selectPendingConsult(@Param("expertId") Long expertId);
}