package com.human.ofim.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.human.ofim.dto.ConsultChatAlertDto;
import com.human.ofim.dto.ConsultChatDto;
import com.human.ofim.dto.ConsultChatMessageDto;
import com.human.ofim.dto.ConsultChatRoomDto;
import com.human.ofim.dto.ConsultDto;
import com.human.ofim.dto.ConsultPendingDto;
import com.human.ofim.mapper.ConsultChatMapper;
import com.human.ofim.service.MembershipService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ConsultChatServiceImpl implements ConsultChatService {

    private final ConsultChatMapper consultChatMapper;
    private final MembershipService membershipService;

    @Override
    public int findOrCreateChatConsult(Long memberId, Long expertId) {
        Integer consultId = consultChatMapper.selectActiveChatConsult(memberId, expertId);

        if (consultId != null) {
            return consultId; // 기존 있으면 차감 X
        }

        membershipService.validateChatConsultAvailable(memberId);

        ConsultDto dto = new ConsultDto();
        dto.setMemberId(memberId);
        dto.setExpertId(expertId);
        dto.setConsultType("채팅");
        dto.setStatus(1);
        dto.setConsultContent("즉시 상담 요청");

        consultChatMapper.insertConsult(dto);

        if (dto.getConsultId() == null) {
            throw new RuntimeException("채팅 상담방 생성에 실패했습니다.");
        }

        membershipService.useChatConsult(memberId);

        return dto.getConsultId().intValue();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isParticipant(int consultId, Long loginMemberId) {
        return consultChatMapper.countConsultParticipant(consultId, loginMemberId) > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canChat(int consultId, Long loginMemberId) {
        if (!isParticipant(consultId, loginMemberId)) {
            return false;
        }

        Integer status = consultChatMapper.selectConsultStatus(consultId);
        return status != null && status == 2;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConsultChatMessageDto> getMessages(int consultId, Long loginMemberId) {
        if (!isParticipant(consultId, loginMemberId)) {
            throw new RuntimeException("해당 상담 채팅에 접근할 수 없습니다.");
        }

        List<ConsultChatMessageDto> list = consultChatMapper.selectMessagesByConsultId(consultId);
        ConsultChatRoomDto room = consultChatMapper.selectChatRoomSummary(consultId, loginMemberId);

        for (ConsultChatMessageDto dto : list) {
            dto.setMine(dto.getSenderId() != null
                    && dto.getSenderId().longValue() == loginMemberId.longValue());

            if (dto.getSenderId() != null && room != null) {
                if (dto.getSenderId().equals(room.getExpertId())) {
                    dto.setDisplayName(dto.getSenderName());
                } else {
                    dto.setDisplayName(dto.getSenderNickname());
                }
            }
        }

        return list;
    }

    @Override
    public ConsultChatMessageDto saveMessage(ConsultChatMessageDto dto, Long loginMemberId) {
        if (dto == null || dto.getConsultId() == null) {
            throw new RuntimeException("잘못된 요청입니다.");
        }

        if (!canChat(dto.getConsultId(), loginMemberId)) {
            throw new RuntimeException("현재 상담 상태에서는 채팅할 수 없습니다.");
        }

        String content = dto.getMessageContent() == null ? "" : dto.getMessageContent().trim();
        if (content.isEmpty()) {
            throw new RuntimeException("메시지를 입력해주세요.");
        }

        dto.setSenderId(loginMemberId);
        dto.setMessageType(dto.getMessageType() == null ? "TALK" : dto.getMessageType());
        dto.setMessageContent(content);

        consultChatMapper.insertMessage(dto);

        dto.setMine(true);
        dto.setIsRead(0);

        ConsultChatRoomDto room = consultChatMapper.selectChatRoomSummary(dto.getConsultId(), loginMemberId);

        if (room != null) {
            if (dto.getSenderId().equals(room.getExpertId())) {
                dto.setDisplayName(room.getExpertName());
            } else {
                dto.setDisplayName(room.getMemberNickname());
            }
        }

        return dto;
    }

    @Override
    public void readMessages(int consultId, Long loginMemberId) {
        if (!isParticipant(consultId, loginMemberId)) {
            throw new RuntimeException("해당 상담 채팅에 접근할 수 없습니다.");
        }

        consultChatMapper.updateReadByConsultId(consultId, loginMemberId);
    }

    @Override
    @Transactional(readOnly = true)
    public int getUnreadCount(int consultId, Long loginMemberId) {
        if (!isParticipant(consultId, loginMemberId)) {
            throw new RuntimeException("해당 상담 채팅에 접근할 수 없습니다.");
        }

        return consultChatMapper.countUnreadByConsultId(consultId, loginMemberId);
    }

    @Override
    @Transactional(readOnly = true)
    public ConsultChatRoomDto getRoomSummary(int consultId, Long loginMemberId) {
        if (!isParticipant(consultId, loginMemberId)) {
            throw new RuntimeException("해당 상담 채팅에 접근할 수 없습니다.");
        }

        ConsultChatRoomDto room = consultChatMapper.selectChatRoomSummary(consultId, loginMemberId);

        if (room == null) {
            room = new ConsultChatRoomDto();
            room.setConsultId(consultId);
            room.setUnreadCount(0);
        }

        return room;
    }

    @Override
    @Transactional(readOnly = true)
    public ConsultChatAlertDto getLatestChatAlert(Long expertId) {
        ConsultChatAlertDto dto = consultChatMapper.selectLatestPendingChatForExpert(expertId);

        if (dto == null) {
            dto = new ConsultChatAlertDto();
            dto.setNewChat(false);
            return dto;
        }

        dto.setNewChat(true);
        return dto;
    }

    @Override
    public void acceptChat(int consultId, Long expertId) {
        if (!isParticipant(consultId, expertId)) {
            throw new RuntimeException("권한 없음");
        }

        ConsultChatDto consult = consultChatMapper.selectConsultDetail(consultId);
        if (consult == null) {
            throw new RuntimeException("상담 정보가 없습니다.");
        }

        if (consult.getStatus() == 2) {
            return;
        }

        if (consult.getStatus() != 1) {
            throw new RuntimeException("대기중인 상담만 수락할 수 있습니다.");
        }

        consultChatMapper.updateStatus(consultId, 2);

        ConsultChatMessageDto msg = new ConsultChatMessageDto();
        msg.setConsultId(consultId);
        msg.setSenderId(expertId);
        msg.setMessageType("SYSTEM");
        msg.setMessageContent("전문가가 즉시 상담을 수락했습니다. 상담을 시작합니다.");
        consultChatMapper.insertMessage(msg);
    }

    @Override
    public void rejectChat(int consultId, Long expertId, String reason) {
        if (!isParticipant(consultId, expertId)) {
            throw new RuntimeException("권한 없음");
        }

        String rejectReason = (reason == null) ? "" : reason.trim();
        if (rejectReason.isEmpty()) {
            throw new RuntimeException("거절 사유를 입력해주세요.");
        }

        ConsultChatDto consult = consultChatMapper.selectConsultDetail(consultId);
        if (consult == null) {
            throw new RuntimeException("상담 정보가 없습니다.");
        }

        if (consult.getStatus() != 1) {
            throw new RuntimeException("대기중인 상담만 거절할 수 있습니다.");
        }

        consultChatMapper.updateStatus(consultId, 4);

        ConsultChatMessageDto rejectMsg = new ConsultChatMessageDto();
        rejectMsg.setConsultId(consultId);
        rejectMsg.setSenderId(expertId);
        rejectMsg.setMessageType("SYSTEM");
        rejectMsg.setMessageContent("전문가가 즉시 상담이 어렵습니다. 사유: " + rejectReason);
        consultChatMapper.insertMessage(rejectMsg);

        ConsultChatMessageDto endMsg = new ConsultChatMessageDto();
        endMsg.setConsultId(consultId);
        endMsg.setSenderId(expertId);
        endMsg.setMessageType("SYSTEM");
        endMsg.setMessageContent("채팅 상담이 종료되었습니다. 상담을 예약하시겠습니까?");
        consultChatMapper.insertMessage(endMsg);
    }

    @Override
    public void completeChat(int consultId, Long expertId) {
        if (!isParticipant(consultId, expertId)) {
            throw new RuntimeException("권한 없음");
        }

        ConsultChatDto consult = consultChatMapper.selectConsultDetail(consultId);
        if (consult == null) {
            throw new RuntimeException("상담 정보가 없습니다.");
        }

        if (consult.getStatus() != 2) {
            throw new RuntimeException("진행중인 상담만 완료 처리할 수 있습니다.");
        }

        consultChatMapper.updateStatus(consultId, 3);

        ConsultChatMessageDto endMsg = new ConsultChatMessageDto();
        endMsg.setConsultId(consultId);
        endMsg.setSenderId(expertId);
        endMsg.setMessageType("SYSTEM");
        endMsg.setMessageContent("상담이 완료되었습니다.");
        consultChatMapper.insertMessage(endMsg);
    }

    @Override
    @Transactional(readOnly = true)
    public ConsultPendingDto getPendingConsult(Long expertId) {
        return consultChatMapper.selectPendingConsult(expertId);
    }
}