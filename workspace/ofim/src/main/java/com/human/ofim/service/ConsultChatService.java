package com.human.ofim.service;

import java.util.List;

import com.human.ofim.dto.ConsultChatAlertDto;
import com.human.ofim.dto.ConsultChatMessageDto;
import com.human.ofim.dto.ConsultChatRoomDto;
import com.human.ofim.dto.ConsultPendingDto;

public interface ConsultChatService {

    int findOrCreateChatConsult(Long memberId, Long expertId);

    List<ConsultChatMessageDto> getMessages(int consultId, Long loginMemberId);

    ConsultChatMessageDto saveMessage(ConsultChatMessageDto dto, Long loginMemberId);

    void readMessages(int consultId, Long loginMemberId);

    int getUnreadCount(int consultId, Long loginMemberId);

    ConsultChatRoomDto getRoomSummary(int consultId, Long loginMemberId);

    boolean isParticipant(int consultId, Long loginMemberId);

    boolean canChat(int consultId, Long loginMemberId);

    ConsultChatAlertDto getLatestChatAlert(Long expertId);

    void acceptChat(int consultId, Long expertId);

    void rejectChat(int consultId, Long expertId, String reason);

    void completeChat(int consultId, Long expertId);

    ConsultPendingDto getPendingConsult(Long expertId);
}