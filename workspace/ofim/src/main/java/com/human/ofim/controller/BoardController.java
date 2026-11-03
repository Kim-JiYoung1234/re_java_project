package com.human.ofim.controller;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.human.ofim.dto.BoardDto;
import com.human.ofim.dto.BoardLikeDto;
import com.human.ofim.dto.BoardReplyDto;
import com.human.ofim.dto.BoardReportDto;
import com.human.ofim.dto.MemberDto;
import com.human.ofim.service.BoardService;
import com.human.ofim.service.RankingService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/community")
public class BoardController {

	private final BoardService boardService;
	private final RankingService rankingService;

	@Value("${file.upload-dir}")
	private String uploadPath;

	// 1. 글쓰기 페이지 진입
	@GetMapping("/write")
	public String writeForm(@RequestParam(value = "categoryId", required = false) Integer categoryId, Model model,
			HttpSession session) {
		if (session.getAttribute("user") == null) {
			return "redirect:/auth/login";
		}

		BoardDto board = new BoardDto();
		if (categoryId != null)
			board.setCategoryId(categoryId);

		model.addAttribute("board", board);
		return "community/write";
	}

	// 2. 글 등록 실행 (포인트 실시간 반영 추가)
	@PostMapping("/write")
	public String writePost(BoardDto boardDto, HttpSession session) throws IOException {
		MemberDto loginMember = (MemberDto) session.getAttribute("user");

		if (loginMember == null) {
			return "redirect:/auth/login";
		}

		String mType = loginMember.getMemberType();
		boolean isAdmin = "관리자".equals(mType);

		if ((boardDto.getCategoryId() == 1 || boardDto.getCategoryId() == 4) && !isAdmin) {
			return "redirect:/community/main?error=no_admin";
		}

		boardDto.setWriterId(loginMember.getMemberId().intValue());

		String savedFileName = saveUploadFile(boardDto.getUploadFile());
		if (savedFileName != null) {
			boardDto.setImageName(savedFileName);
		}

		applyDefaultSubCategory(boardDto, mType);
		processChallengeLogic(boardDto, isAdmin);

		// 🌟 [수정] 서비스에서 최신 유저 정보를 반환받아 세션 갱신
		MemberDto updatedUser = boardService.save(boardDto);
		if (updatedUser != null) {
			session.setAttribute("user", updatedUser);
		}

		return "redirect:/community/main?categoryId=" + boardDto.getCategoryId();
	}

	// 3. 커뮤니티 메인 (트로피 전용 내 랭킹 조회 추가)
	@GetMapping("/main")
	public String communityMain(@RequestParam(value = "categoryId", required = false) Integer categoryId,
			@RequestParam(value = "subCategory", defaultValue = "전체") String subCategory,
			@RequestParam(value = "keyword", required = false) String keyword,
			@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "sort", defaultValue = "latest") String sort, Model model, HttpServletRequest request,
			HttpSession session) {

		if (page < 1)
			page = 1;

		MemberDto loginMember = (MemberDto) session.getAttribute("user");

		if (loginMember != null) {
			int memberId = loginMember.getMemberId().intValue();
			int postCount = boardService.getMyPostCount(memberId);
			int replyCount = boardService.getMyReplyCount(memberId);

			model.addAttribute("postCount", postCount);
			model.addAttribute("replyCount", replyCount);

			// 🌟 [추가] 내 전체 랭킹 순위 조회
			int myRank = rankingService.getMemberRank(memberId);
			model.addAttribute("myRanking", myRank);
		} else {
			model.addAttribute("postCount", 0);
			model.addAttribute("replyCount", 0);
			model.addAttribute("myRanking", 0);
		}

		List<BoardDto> sideNotices = boardService.findAll(1, "전체", null, 1, "latest");
		if (sideNotices != null && sideNotices.size() > 3) {
			sideNotices = sideNotices.subList(0, 3);
		}
		model.addAttribute("sideNotices", sideNotices);

		List<BoardDto> topRankers = rankingService.getTopRankers();
		model.addAttribute("topRankers", topRankers != null ? topRankers : new java.util.ArrayList<>());

		List<BoardDto> boardList = boardService.findAll(categoryId, subCategory, keyword, page, sort);
		int totalCount = boardService.getTotalCount(categoryId, subCategory, keyword);
		int totalPages = (totalCount > 0) ? (int) Math.ceil((double) totalCount / 20) : 0;

		model.addAttribute("boardList", boardList);
		model.addAttribute("currentCategory", categoryId);
		model.addAttribute("currentSub", subCategory);
		model.addAttribute("keyword", keyword);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("currentSort", sort);

		String ajaxHeader = request.getHeader("X-Requested-With");
		if ("XMLHttpRequest".equals(ajaxHeader)) {
			return "community/community :: #boardContent";
		}

		return "community/community";
	}

	// 4. 상세 보기
	@GetMapping("/detail/{postId}")
	public String detail(@PathVariable("postId") int postId, Model model, HttpSession session,
			HttpServletRequest request, HttpServletResponse response) {

		Cookie[] cookies = request.getCookies();
		boolean hasVisited = false;

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ("postView".equals(cookie.getName()) && cookie.getValue().contains("[" + postId + "]")) {
					hasVisited = true;
					break;
				}
			}
		}

		if (!hasVisited) {
			boardService.incrementHit(postId);
			String cookieValue = "";
			Cookie oldCookie = null;

			if (cookies != null) {
				for (Cookie c : cookies) {
					if ("postView".equals(c.getName())) {
						oldCookie = c;
						break;
					}
				}
			}

			if (oldCookie != null) {
				cookieValue = oldCookie.getValue() + "[" + postId + "]";
			} else {
				cookieValue = "[" + postId + "]";
			}

			Cookie newCookie = new Cookie("postView", cookieValue);
			newCookie.setPath("/");
			newCookie.setMaxAge(60 * 60 * 24);
			newCookie.setHttpOnly(true);
			response.addCookie(newCookie);
		}

		BoardDto boardDto = boardService.findDetailById(postId);

		MemberDto loginMember = (MemberDto) session.getAttribute("user");
		Integer memberId = (loginMember != null) ? loginMember.getMemberId().intValue() : null;
		List<BoardReplyDto> replyList = boardService.getReplyList(postId, memberId);

		int likeCount = boardService.getLikeCount(postId);
		boolean isLiked = false;

		if (loginMember != null) {
			isLiked = boardService.isLiked(postId, loginMember.getMemberId().intValue());
		}

		model.addAttribute("board", boardDto);
		model.addAttribute("replyList", replyList);
		model.addAttribute("likeCount", likeCount);
		model.addAttribute("isLiked", isLiked);

		return "community/detail";
	}

	// 5. 게시글 삭제 처리
	@GetMapping("/delete/{postId}")
	public String deletePost(@PathVariable("postId") int postId, HttpSession session) {
		MemberDto loginMember = (MemberDto) session.getAttribute("user");
		if (loginMember == null)
			return "redirect:/auth/login";

		BoardDto boardDto = boardService.getBoardById(postId);

		boolean isWriter = loginMember.getMemberId().intValue() == boardDto.getWriterId();
		boolean isAdmin = "관리자".equals(loginMember.getMemberType());

		if (isWriter || isAdmin) {
			MemberDto updatedUser = boardService.delete(postId);
			
			if (isWriter && updatedUser != null) {
				session.setAttribute("user", updatedUser);
			}
			return "redirect:/community/main?categoryId=" + boardDto.getCategoryId();
		}

		return "redirect:/community/main?error=no_permission";
	}

	// 6. 수정 페이지 진입
	@GetMapping("/update/{postId}")
	public String updateForm(@PathVariable("postId") int postId, HttpSession session, Model model) {
		MemberDto loginMember = (MemberDto) session.getAttribute("user");
		if (loginMember == null)
			return "redirect:/auth/login";

		BoardDto boardDto = boardService.getBoardById(postId);

		boolean isWriter = loginMember.getMemberId().intValue() == boardDto.getWriterId();
		boolean isAdmin = "관리자".equals(loginMember.getMemberType());

		if (isWriter || isAdmin) {
			model.addAttribute("board", boardDto);
			return "community/write";
		}

		return "redirect:/community/main?error=no_permission";
	}

	// 7. 수정 실행
	@PostMapping("/update")
	public String update(BoardDto boardDto, HttpSession session) throws IOException {
		if (boardDto.getPostId() == 0)
			return "redirect:/community/main";

		MemberDto loginMember = (MemberDto) session.getAttribute("user");
		if (loginMember == null)
			return "redirect:/auth/login";

		BoardDto existingBoard = boardService.getBoardById(boardDto.getPostId());
		String mType = loginMember.getMemberType();
		boolean isAdmin = "관리자".equals(mType);
		boolean isWriter = loginMember.getMemberId().intValue() == existingBoard.getWriterId();

		if (!(isWriter || isAdmin))
			return "redirect:/community/main?error=no_permission";

		MultipartFile file = boardDto.getUploadFile();
		if (file != null && !file.isEmpty()) {
			boardDto.setImageName(saveUploadFile(file));
		} else {
			boardDto.setImageName(existingBoard.getImageName());
		}

		if (boardDto.getCategoryId() == 0)
			boardDto.setCategoryId(existingBoard.getCategoryId());
		boardDto.setWriterId(existingBoard.getWriterId());

		applyDefaultSubCategory(boardDto, mType);
		processChallengeLogic(boardDto, isAdmin);

		boardService.update(boardDto);
		return "redirect:/community/detail/" + boardDto.getPostId();
	}

	// 8. 댓글/대댓글 등록
	@PostMapping("/reply/write")
	public String replyWrite(BoardReplyDto replyDto, HttpSession session) {
		MemberDto loginMember = (MemberDto) session.getAttribute("user");
		if (loginMember == null)
			return "redirect:/auth/login";

		replyDto.setWriterId(loginMember.getMemberId().intValue());

		MemberDto updatedUser = boardService.saveReply(replyDto);
		if (updatedUser != null) {
			session.setAttribute("user", updatedUser);
		}

		return "redirect:/community/detail/" + replyDto.getPostId();
	}

	// 🌟 [추가] 8-1. 댓글/대댓글 수정 (Fetch/AJAX 요청 처리)
	@PostMapping("/reply/update")
	@ResponseBody
	public Map<String, Object> replyUpdate(@RequestParam("replyId") int replyId,
			@RequestParam("content") String content, HttpSession session) {
		Map<String, Object> result = new HashMap<>();
		MemberDto loginMember = (MemberDto) session.getAttribute("user");

		if (loginMember == null) {
			result.put("status", "error");
			result.put("message", "login_required");
			return result;
		}

		// 본인 확인 로직 (서비스에서 처리 권장)
		boolean isWriter = boardService.isReplyWriter(replyId, loginMember.getMemberId().intValue());
		boolean isAdmin = "관리자".equals(loginMember.getMemberType());

		if (!isWriter && !isAdmin) {
			result.put("status", "error");
			result.put("message", "no_permission");
			return result;
		}

		try {
			boardService.updateReply(replyId, content);
			result.put("status", "success");
		} catch (Exception e) {
			result.put("status", "error");
			result.put("message", "fail");
		}

		return result;
	}

	// 9. 댓글 삭제
	@GetMapping("/reply/delete/{replyId}")
	public String deleteReply(@PathVariable("replyId") int replyId, @RequestParam("postId") int postId,
			HttpSession session) {
		MemberDto loginMember = (MemberDto) session.getAttribute("user");
		if (loginMember == null)
			return "redirect:/auth/login";

		boolean isAdmin = "관리자".equals(loginMember.getMemberType());
		boolean isReplyWriter = boardService.isReplyWriter(replyId, loginMember.getMemberId().intValue());

		if (isAdmin || isReplyWriter) {
			MemberDto updatedUser = boardService.removeReply(replyId);
			if (updatedUser != null) {
				session.setAttribute("user", updatedUser);
			}
		}

		return "redirect:/community/detail/" + postId;
	}

	// 10. 댓글/대댓글 좋아요 토글
	@GetMapping("/reply/like/{replyId}")
	@ResponseBody
	public Map<String, Object> toggleReplyLike(@PathVariable("replyId") int replyId, HttpSession session) {
		Map<String, Object> result = new HashMap<>();
		MemberDto loginMember = (MemberDto) session.getAttribute("user");

		if (loginMember == null) {
			result.put("count", -1);
			return result;
		}

		int memberId = loginMember.getMemberId().intValue();

		boolean liked;
		if (boardService.checkReplyLike(replyId, memberId) == 0) {
			boardService.addReplyLike(replyId, memberId);
			liked = true;
		} else {
			boardService.removeReplyLike(replyId, memberId);
			liked = false;
		}

		result.put("count", boardService.getReplyLikeCount(replyId));
		result.put("liked", liked);
		return result;
	}

	// 11. 게시글 좋아요 토글
	@GetMapping("/like/{postId}")
	@ResponseBody
	public Map<String, Object> toggleLike(@PathVariable("postId") int postId, HttpSession session) {
		Map<String, Object> result = new HashMap<>();
		MemberDto loginMember = (MemberDto) session.getAttribute("user");

		if (loginMember == null) {
			result.put("count", -1);
			return result;
		}

		BoardLikeDto likeDto = new BoardLikeDto();
		likeDto.setPostId(postId);
		likeDto.setMemberId(loginMember.getMemberId().intValue());

		boolean liked;
		MemberDto updatedUser;

		if (boardService.checkLike(likeDto) == 0) {
			updatedUser = boardService.addLike(likeDto);
			liked = true;
		} else {
			updatedUser = boardService.removeLike(likeDto);
			liked = false;
		}

		if (updatedUser != null) {
			session.setAttribute("user", updatedUser);
		}

		result.put("count", boardService.getLikeCount(postId));
		result.put("liked", liked);
		return result;
	}

	// 12. 게시글 신고 처리
	@PostMapping("/report")
	@ResponseBody
	public String report(BoardReportDto reportDto, HttpSession session) {
		MemberDto user = (MemberDto) session.getAttribute("user");
		if (user == null)
			return "login_required";

		reportDto.setMemberId(user.getMemberId().intValue());

		boolean result = boardService.reportPost(reportDto);
		return result ? "success" : "already_reported";
	}

	@GetMapping("/mypage/posts/fragment")
	public String getMyPostsFragment(@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "keyword", required = false) String keyword, Model model) {
		int memberId = 1;
		List<BoardDto> myPosts = boardService.findAll(null, "전체", keyword, page, "latest", memberId);
		model.addAttribute("myPosts", myPosts);
		return "mypage/main :: my-posts-fragment";
	}

	// ===================== Helpers =====================

	private void processChallengeLogic(BoardDto boardDto, boolean isAdmin) {
		if (boardDto.getCategoryId() == 6) {
			if (isAdmin) {
				if (boardDto.getTitle() != null && boardDto.getTitle().contains("[공식]")) {
					boardDto.setSubCategory("공식 챌린지");
				}
			} else {
				if ("공식 챌린지".equals(boardDto.getSubCategory())) {
					boardDto.setSubCategory("자율 챌린지");
				}
				if (boardDto.getTitle() != null && boardDto.getTitle().contains("[공식]")) {
					boardDto.setTitle(boardDto.getTitle().replace("[공식]", "[자율]"));
				}
			}
		}
	}

	private String saveUploadFile(MultipartFile file) throws IOException {
		if (file == null || file.isEmpty())
			return null;

		File folder = new File(uploadPath);
		if (!folder.exists())
			folder.mkdirs();

		String saveName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
		file.transferTo(new File(folder, saveName));
		return saveName;
	}

	private void applyDefaultSubCategory(BoardDto boardDto, String memberType) {
		if (boardDto.getCategoryId() == 1) {
			boardDto.setSubCategory("공지");
			return;
		}
		if (boardDto.getSubCategory() == null || boardDto.getSubCategory().trim().isEmpty()) {
			switch (boardDto.getCategoryId()) {
			case 2:
				boardDto.setSubCategory("일상/잡담");
				break;
			case 3:
				boardDto.setSubCategory("일상생활");
				break;
			case 4:
				boardDto.setSubCategory("건강백과");
				break;
			case 6:
				boardDto.setSubCategory("관리자".equals(memberType) ? "공식 챌린지" : "자율 챌린지");
				break;
			default:
				boardDto.setSubCategory("기타");
				break;
			}
		}
	}
}