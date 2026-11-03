package com.human.ofim.service;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.human.ofim.dto.MemberDto;
import com.human.ofim.mapper.AdminUserMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final AdminUserMapper adminUserMapper;

    private static final Set<String> ALLOWED_STATUSES = Set.of(
            "가입중",
            "징계정지",
            "사고정지",
            "자발적탈퇴",
            "징계탈퇴",
            "사고탈퇴"
    );

    @Override
    public List<MemberDto> getUserList(String type, String grade, String status, String keyword, int offset, int size) {
        return adminUserMapper.selectUserList(type, grade, status, keyword, offset, size);
    }

    @Override
    public int getUserCount(String type, String grade, String status, String keyword) {
        return adminUserMapper.countUsers(type, grade, status, keyword);
    }

    @Override
    public MemberDto getUserDetail(Long memberId) {
        return adminUserMapper.selectUserDetail(memberId);
    }

    @Override
    @Transactional
    public void updateMemberStatus(int memberId, String memberStatus, String reason) {

        if (!ALLOWED_STATUSES.contains(memberStatus)) {
            throw new IllegalArgumentException("허용되지 않은 회원 상태값입니다.");
        }

        int result = adminUserMapper.updateMemberStatus(memberId, memberStatus, reason);

        if (result <= 0) {
            throw new IllegalArgumentException("상태를 변경할 회원이 존재하지 않습니다.");
        }
    }
}