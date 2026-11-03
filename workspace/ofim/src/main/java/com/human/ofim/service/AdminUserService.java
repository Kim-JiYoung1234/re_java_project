package com.human.ofim.service;

import java.util.List;

import com.human.ofim.dto.MemberDto;

public interface AdminUserService {

    List<MemberDto> getUserList(
            String type,
            String grade,
            String status,
            String keyword,
            int offset,
            int size
    );

    int getUserCount(
            String type,
            String grade,
            String status,
            String keyword
    );

    MemberDto getUserDetail(Long memberId);

    void updateMemberStatus(int memberId, String memberStatus, String reason);
}