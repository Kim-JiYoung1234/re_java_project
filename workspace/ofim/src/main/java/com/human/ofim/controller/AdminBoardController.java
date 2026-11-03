package com.human.ofim.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.human.ofim.dto.BoardDto;
import com.human.ofim.dto.BoardReportDto;
import com.human.ofim.service.BoardService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminBoardController {

	private final BoardService boardService;

	/**
	 * 게시글 관리 페이지 (boards.html 연동)
	 */
	@GetMapping("/boards")
	public String boards(@RequestParam(value = "keyword", required = false) String keyword,
			@RequestParam(value = "categoryId", required = false) Integer categoryId,
			@RequestParam(value = "deleted", required = false) Integer deleted,
			@RequestParam(value = "page", defaultValue = "1") int page, Model model) {
		if (page < 1)
			page = 1;

		int size = 20;
		int offset = (page - 1) * size;

		// 1. 게시글 목록 조회 (카테고리/상태 필터 포함)
		List<BoardDto> posts = boardService.adminPostList(keyword, categoryId, deleted, offset, size);

		// 2. 전체 건수 조회
		int totalCount = boardService.adminPostCount(keyword, categoryId, deleted);
		int totalPages = (int) Math.ceil((double) totalCount / size);

		// 3. 카테고리 리스트 (검색 조건용)
		model.addAttribute("categoryList", boardService.getCategoryList());

		// HTML(boards.html)에서 사용하는 변수명으로 매핑
		model.addAttribute("posts", posts);
		model.addAttribute("totalCount", totalCount);
		model.addAttribute("page", page);
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("keyword", keyword);
		model.addAttribute("categoryId", categoryId);
		model.addAttribute("deleted", deleted);

		model.addAttribute("pageTitle", "게시글 관리");
		model.addAttribute("activeMenu", "boards");

		return "admin/boards";
	}

	/**
	 * 게시글 숨김 처리 (단일)
	 */
	@PostMapping("/boards/hide")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> hidePost(@RequestParam("postId") int postId) {
		boardService.updateDeletedStatus(postId, 1);
		Map<String, Object> result = new HashMap<>();
		result.put("success", true);
		return ResponseEntity.ok(result);
	}

	/**
	 * 게시글 복구 처리 (단일)
	 */
	@PostMapping("/boards/restore")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> restorePost(@RequestParam("postId") int postId) {
		boardService.updateDeletedStatus(postId, 0);
		Map<String, Object> result = new HashMap<>();
		result.put("success", true);
		return ResponseEntity.ok(result);
	}

	/**
	 * ✅ [추가] 게시글 일괄 숨김 처리
	 */
	@PostMapping("/boards/batch-hide")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> batchHide(@RequestBody List<Integer> postIds) {
		postIds.forEach(id -> boardService.updateDeletedStatus(id, 1));
		Map<String, Object> result = new HashMap<>();
		result.put("success", true);
		return ResponseEntity.ok(result);
	}

	/**
	 * ✅ [추가] 게시글 일괄 복구 처리
	 */
	@PostMapping("/boards/batch-restore")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> batchRestore(@RequestBody List<Integer> postIds) {
		postIds.forEach(id -> boardService.updateDeletedStatus(id, 0));
		Map<String, Object> result = new HashMap<>();
		result.put("success", true);
		return ResponseEntity.ok(result);
	}

	/**
	 * ✅ [추가] 게시글 일괄 영구 삭제 (진짜 DB DELETE)
	 */
	@PostMapping("/boards/batch-delete")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> batchDelete(@RequestBody List<Integer> postIds) {
		// 기존 매퍼의 deleteById 로직이 '삭제'라면 그대로 사용
		postIds.forEach(id -> boardService.deleteBoard(id));
		Map<String, Object> result = new HashMap<>();
		result.put("success", true);
		return ResponseEntity.ok(result);
	}

	/* 신고 관리 페이지 (기존 유지) */
	@GetMapping("/reports")
	public String reports(Model model) {
		List<BoardReportDto> reports = boardService.getAllReports();
		model.addAttribute("reports", reports);
		model.addAttribute("totalCount", reports.size());
		model.addAttribute("pageTitle", "신고 관리");
		model.addAttribute("activeMenu", "reports");
		return "admin/reports";
	}

	/* 신고 상태/메모 수정 (기존 유지) */
	@PostMapping("/reports/update")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> updateReport(@RequestParam("postId") int postId,
			@RequestParam("memberId") int memberId, @RequestParam("status") int status,
			@RequestParam(value = "adminMemo", required = false) String adminMemo) {
		boardService.updateReportStatus(postId, memberId, status, adminMemo);
		Map<String, Object> result = new HashMap<>();
		result.put("success", true);
		result.put("message", "신고 상태가 저장되었습니다.");
		return ResponseEntity.ok(result);
	}
}