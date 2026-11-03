package com.human.ofim.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.human.ofim.dto.MemberDto;
import com.human.ofim.service.BookmarkService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/bookmark")
public class BookmarkController {

	@Autowired
	private BookmarkService bookmarkService;

	/**
	 * 북마크 토글 API @PathVariable("postId") 처럼 이름을 명시해야 Spring Boot 3에서 에러가 안 납니다!
	 */
	@PostMapping("/{postId}")
	public ResponseEntity<?> toggleBookmark(@PathVariable("postId") int postId, HttpSession session) {

		// 1. 세션에서 로그인 정보를 가져옴 (MemberDto의 memberId는 Long 타입)
		MemberDto member = (MemberDto) session.getAttribute("user");

		if (member == null) {
			// 로그인이 안 되어 있으면 401 에러와 메시지 반환
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
		}

		try {
			// 2. 서비스 호출 (member.getMemberId()는 이제 Long을 반환함)
			// 서비스와 매퍼가 Long을 받도록 수정되어 있어야 합니다.
			boolean isAdded = bookmarkService.toggleBookmark(member.getMemberId(), postId);

			// 성공 시 "added" 또는 "removed" 문자열 반환
			return ResponseEntity.ok(isAdded ? "added" : "removed");

		} catch (Exception e) {
			// 예외 발생 시 콘솔에 로그를 찍고 500 에러 반환
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
		}
	}
}