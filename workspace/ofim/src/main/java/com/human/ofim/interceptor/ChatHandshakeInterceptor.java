package com.human.ofim.interceptor;

import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.human.ofim.dto.MemberDto;

import jakarta.servlet.http.HttpSession;

public class ChatHandshakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
						            ServerHttpResponse response,
						            WebSocketHandler wsHandler,
						            Map<String, Object> attributes) {

        if (request instanceof ServletServerHttpRequest servletRequest) {
            HttpSession session = servletRequest.getServletRequest().getSession(false);

            if (session != null) {
                MemberDto loginUser = (MemberDto) session.getAttribute("loginUser");
                if (loginUser == null) {
                    loginUser = (MemberDto) session.getAttribute("user");
                }

                if (loginUser != null) {
                    attributes.put("loginMemberId", loginUser.getMemberId());
                }
            }
        }

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
					            ServerHttpResponse response,
					            WebSocketHandler wsHandler,
					            Exception exception) {
    }
}