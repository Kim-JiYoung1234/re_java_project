package com.human.ofim.listener;

import org.springframework.stereotype.Component;

import com.human.ofim.dto.MemberDto;
import com.human.ofim.mapper.ExpertUserMapper;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ExpertOnlineSessionListener implements HttpSessionListener {

    private final ExpertUserMapper expertUserMapper;

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        Object obj = se.getSession().getAttribute("loginUser");

        if (obj instanceof MemberDto loginUser) {
            if ("EXPERT".equals(loginUser.getMemberType())) {
                expertUserMapper.updateOnlineStatus(loginUser.getMemberId(), false);
            }
        }
    }
}