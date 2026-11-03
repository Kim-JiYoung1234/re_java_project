package com.human.ofim.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.human.ofim.dto.BoardDto;
import com.human.ofim.dto.BoardLikeDto;
import com.human.ofim.dto.BoardReplyDto;
import com.human.ofim.dto.BoardReportDto;
import com.human.ofim.dto.MemberDto;
import com.human.ofim.mapper.BoardMapper;
import com.human.ofim.mapper.MemberMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

	private final BoardMapper boardMapper;
	private final MemberMapper memberMapper;

	// 🛡️ 하루 최대 적립 가능 포인트 설정
	private static final int DAILY_MAX_POINTS = 50;

	/* ===================== 게시글 관련 ===================== */

	public List<BoardDto> findAll(Integer categoryId, String subCategory, String keyword, int page, String sort) {
		int size = 20;
		int startRow = (page - 1) * size;
		return boardMapper.selectAll(categoryId, subCategory, keyword, startRow, size, sort, null);
	}

	public List<BoardDto> findAll(Integer categoryId, String subCategory, String keyword, int page, String sort,
			Integer writerId) {
		int size = 8;
		int startRow = (page - 1) * size;
		return boardMapper.selectAll(categoryId, subCategory, keyword, startRow, size, sort, writerId);
	}

	@Transactional
	public MemberDto save(BoardDto boardDto) {
		boardMapper.insertBoard(boardDto);
		int points = (boardDto.getCategoryId() == 6) ? 30 : 10;
		String reason = (boardDto.getCategoryId() == 6) ? "챌린지 글 작성" : "일반 글 작성";
		return applySafePointUpdate(boardDto.getWriterId(), points, reason);
	}

	public int getTotalCount(Integer categoryId, String subCategory, String keyword) {
		return getTotalCount(categoryId, subCategory, keyword, null);
	}

	public int getTotalCount(Integer categoryId, String subCategory, String keyword, Integer writerId) {
		return boardMapper.countAll(categoryId, subCategory, keyword, writerId);
	}

	public BoardDto findDetailById(int postId) {
		BoardDto boardDto = boardMapper.selectById(postId);
		if (boardDto == null) {
			throw new RuntimeException("해당 게시글이 존재하지 않습니다.");
		}
		return boardDto;
	}

	public BoardDto getBoardById(int postId) {
		BoardDto boardDto = boardMapper.selectById(postId);
		if (boardDto == null) {
			throw new RuntimeException("해당 게시글이 존재하지 않습니다.");
		}
		return boardDto;
	}

	@Transactional
	public MemberDto delete(int postId) {
		BoardDto boardDto = boardMapper.selectById(postId);
		if (boardDto != null) {
			int pointsToDeduct = (boardDto.getCategoryId() == 6) ? -30 : -10;
			memberMapper.updateActivityPoint(pointsToDeduct, boardDto.getWriterId());
			memberMapper.insertPointLog(boardDto.getWriterId(), pointsToDeduct, "게시글 삭제(회수)");
			boardMapper.deleteById(postId);
			return memberMapper.selectMemberById(boardDto.getWriterId());
		}
		return null;
	}

	@Transactional
	public void update(BoardDto boardDto) {
		int result = boardMapper.updateBoard(boardDto);
		if (result == 0) {
			throw new RuntimeException("게시글 수정에 실패했습니다.");
		}
	}

	/* ===================== 댓글 관련 ===================== */

	public List<BoardReplyDto> getReplyList(int postId, Integer memberId) {
		Map<String, Object> params = new HashMap<>();
		params.put("postId", postId);
		params.put("memberId", memberId);
		return boardMapper.selectRepliesByPostId(params);
	}

	@Transactional
	public MemberDto saveReply(BoardReplyDto replyDto) {
		boardMapper.insertReply(replyDto);
		return applySafePointUpdate(replyDto.getWriterId(), 2, "댓글 작성");
	}

	// 🌟 [추가] 댓글 수정 로직
	@Transactional
	public void updateReply(int replyId, String content) {
		int result = boardMapper.updateReply(replyId, content);
		if (result == 0) {
			throw new RuntimeException("댓글 수정에 실패했습니다.");
		}
	}

	@Transactional
	public MemberDto removeReply(int replyId) {
		Integer writerId = boardMapper.selectReplyWriterId(replyId);
		if (writerId != null) {
			memberMapper.updateActivityPoint(-2, writerId);
			memberMapper.insertPointLog(writerId, -2, "댓글 삭제(회수)");
			boardMapper.deleteReplyById(replyId);
			return memberMapper.selectMemberById(writerId);
		}
		return null;
	}

	public boolean isReplyWriter(int replyId, int memberId) {
		return boardMapper.checkReplyWriter(replyId, memberId) > 0;
	}

	/* ===================== 댓글 좋아요 관련 ===================== */

	public int checkReplyLike(int replyId, int memberId) {
		return boardMapper.checkReplyLike(replyId, memberId);
	}

	@Transactional
	public void addReplyLike(int replyId, int memberId) {
		boardMapper.insertReplyLike(replyId, memberId);
	}

	@Transactional
	public void removeReplyLike(int replyId, int memberId) {
		boardMapper.deleteReplyLike(replyId, memberId);
	}

	public int getReplyLikeCount(int replyId) {
		return boardMapper.countReplyLikes(replyId);
	}

	/* ===================== 게시글 좋아요 관련 ===================== */

	public int checkLike(BoardLikeDto likeDto) {
		return boardMapper.checkLike(likeDto.getPostId(), likeDto.getMemberId());
	}

	@Transactional
	public MemberDto addLike(BoardLikeDto likeDto) {
		boardMapper.insertLike(likeDto.getPostId(), likeDto.getMemberId());

		BoardDto board = boardMapper.selectById(likeDto.getPostId());
		if (board != null && board.getWriterId() != likeDto.getMemberId()) {
			applySafePointUpdate(board.getWriterId(), 5, "좋아요 수혜");
		}
		return memberMapper.selectMemberById(likeDto.getMemberId());
	}

	@Transactional
	public MemberDto removeLike(BoardLikeDto likeDto) {
		BoardDto board = boardMapper.selectById(likeDto.getPostId());
		if (board != null) {
			memberMapper.updateActivityPoint(-5, board.getWriterId());
			memberMapper.insertPointLog(board.getWriterId(), -5, "좋아요 취소(회수)");
			boardMapper.deleteLike(likeDto.getPostId(), likeDto.getMemberId());
		}
		return memberMapper.selectMemberById(likeDto.getMemberId());
	}

	public int getLikeCount(int postId) {
		return boardMapper.countLikes(postId);
	}

	public boolean isLiked(int postId, int memberId) {
		return boardMapper.checkLike(postId, memberId) > 0;
	}

	/* ===================== 🛡️ 핵심 방어 메서드 (공통) ===================== */

	private MemberDto applySafePointUpdate(int memberId, int pointsToAdd, String reason) {
		int todayEarned = memberMapper.getTodayEarnedPoints(memberId);

		if (todayEarned < DAILY_MAX_POINTS) {
			int remainingQuota = DAILY_MAX_POINTS - todayEarned;
			int finalPoints = Math.min(pointsToAdd, remainingQuota);

			memberMapper.updateActivityPoint(finalPoints, memberId);
			memberMapper.insertPointLog(memberId, finalPoints, reason);
		}

		return memberMapper.selectMemberById(memberId);
	}

	/* ===================== 기타 관리자 및 마이페이지 로직 ===================== */

	@Transactional
	public boolean reportPost(BoardReportDto reportDto) {
		int count = boardMapper.checkReport(reportDto.getPostId(), reportDto.getMemberId());
		if (count > 0)
			return false;
		boardMapper.insertReport(reportDto);
		return true;
	}

	public BoardDto getPost(int postId) {
		return boardMapper.selectPost(postId);
	}

	// 🌟 [수정] 카테고리와 삭제 여부 필터 추가
	public List<BoardDto> adminPostList(String keyword, Integer categoryId, Integer deleted, int offset, int size) {
	    return boardMapper.adminPostList(keyword, categoryId, deleted, offset, size);
	}

	// 🌟 [수정] 카테고리와 삭제 여부 필터 추가
	public int adminPostCount(String keyword, Integer categoryId, Integer deleted) {
	    return boardMapper.adminPostCount(keyword, categoryId, deleted);
	}

	// 🌟 [추가] 게시글 상태 변경 (숨김/복구) 로직
	@Transactional
	public void updateDeletedStatus(int postId, int status) {
	    boardMapper.updateDeletedStatus(postId, status);
	}
	
	// 🌟 [추가] 관리자 검색창용 카테고리 목록 조회
	public List<Map<String, Object>> getCategoryList() {
	    return boardMapper.selectCategoryList();
	}
	
	public List<BoardReportDto> getAllReports() {
		return boardMapper.selectAllReports();
	}

	@Transactional
	public void updateReportStatus(int postId, int memberId, int status, String adminMemo) {
		int result = boardMapper.updateReportStatus(postId, memberId, status, adminMemo);
		if (result == 0)
			throw new RuntimeException("신고 상태 수정에 실패했습니다.");
	}

	public int getMyPostCount(int writerId) {
		return boardMapper.countMyPosts(writerId);
	}

	public int getMyReplyCount(int writerId) {
		return boardMapper.countMyReplies(writerId);
	}

	@Transactional
	public void incrementHit(int postId) {
		boardMapper.updateHit(postId);
	}

	public List<BoardDto> getMyPosts(int writerId) {
		return boardMapper.selectByWriterId(writerId);
	}

	@Transactional
	public boolean deleteMyPost(int postId, int memberId) {
		return boardMapper.deleteMyPost(postId, memberId) > 0;
	}

	public List<BoardReplyDto> getMyReplies(int writerId) {
		return boardMapper.selectMyReplies(writerId);
	}

	public List<BoardReplyDto> getMyRepliesPaging(int memberId, String keyword, int page, int size) {
		int startRow = (page - 1) * size;
		return boardMapper.selectMyRepliesPaging(memberId, keyword, startRow, size);
	}

	public int countMyReplies(int memberId, String keyword) {
		return boardMapper.countMyRepliesPaging(memberId, keyword);
	}

	@Transactional
	public boolean deleteMyReply(int replyId, int memberId) {
		return boardMapper.deleteMyReply(replyId, memberId) > 0;
	}

	public List<BoardDto> getMyLikedPosts(int memberId) {
		return boardMapper.selectMyLikedPosts(memberId);
	}

	public List<BoardDto> getMyLikedPostsPaging(int memberId, String keyword, int page, int size) {
		int startRow = (page - 1) * size;
		return boardMapper.selectMyLikedPostsPaging(memberId, keyword, startRow, size);
	}

	public int countMyLikedPostsPaging(int memberId, String keyword) {
		return boardMapper.countMyLikedPostsPaging(memberId, keyword);
	}

	@Transactional
	public boolean deleteMyLike(int postId, int memberId) {
		return boardMapper.deleteMyLike(postId, memberId) > 0;
	}
	
	/**
     * ✅ [추가] 관리자용 게시글 삭제 (진짜 지우지 않고 '숨김' 처리)
     */
    @Transactional
    public void deleteBoard(int postId) {
        // DB에서 데이터를 지우는 대신, 상태값을 1(숨김)로 업데이트합니다.
        boardMapper.updateDeletedStatus(postId, 1);
    }
}