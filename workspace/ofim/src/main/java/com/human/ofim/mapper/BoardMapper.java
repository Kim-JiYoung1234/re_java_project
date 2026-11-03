package com.human.ofim.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.human.ofim.dto.BoardDto;
import com.human.ofim.dto.BoardLikeDto;
import com.human.ofim.dto.BoardReplyDto;
import com.human.ofim.dto.BoardReportDto;

@Mapper
public interface BoardMapper {

	// ===================== 게시글 관련 =====================

	// 게시글 저장
	void insertBoard(BoardDto boardDto);

	// 포인트 방어 로직
	Integer selectReplyWriterId(int replyId);

	// 목록 조회
	List<BoardDto> selectAll(@Param("categoryId") Integer categoryId, @Param("subCategory") String subCategory,
			@Param("keyword") String keyword, @Param("startRow") int startRow, @Param("size") int size,
			@Param("sort") String sort, @Param("writerId") Integer writerId);

	// 게시글 상세 조회
	BoardDto selectById(int postId);

	// 특정 사용자가 작성한 게시글 목록 조회
	List<BoardDto> selectByWriterId(int writerId);

	// 조회수 증가
	void updateViewCount(int postId);

	// 게시글 삭제(논리삭제)
	void deleteById(int postId);

	// 게시글 수정
	int updateBoard(BoardDto boardDto);

	// 전체 개수 조회
	int countAll(@Param("categoryId") Integer categoryId, @Param("subCategory") String subCategory,
			@Param("keyword") String keyword, @Param("writerId") Integer writerId);

	// ===================== 게시글 좋아요 관련 =====================

	// 좋아요 확인
	int checkLike(@Param("postId") int postId, @Param("memberId") int memberId);

	// 좋아요 추가
	void insertLike(@Param("postId") int postId, @Param("memberId") int memberId);

	// 좋아요 취소
	void deleteLike(@Param("postId") int postId, @Param("memberId") int memberId);

	// 좋아요 수
	int countLikes(int postId);

	// 내가 좋아요한 게시글 목록 조회
	List<BoardDto> selectMyLikedPosts(int memberId);

	List<BoardDto> selectMyLikedPostsPaging(@Param("memberId") int memberId, @Param("keyword") String keyword,
			@Param("startRow") int startRow, @Param("size") int size);

	int countMyLikedPostsPaging(@Param("memberId") int memberId, @Param("keyword") String keyword);

	// ===================== 댓글 및 대댓글 관련 =====================

	// 댓글 목록 조회
	List<BoardReplyDto> selectRepliesByPostId(Map<String, Object> params);

	List<BoardReplyDto> selectMyReplies(int writerId);

	List<BoardReplyDto> selectMyRepliesPaging(@Param("memberId") int memberId, @Param("keyword") String keyword,
			@Param("startRow") int startRow, @Param("size") int size);

	int countMyRepliesPaging(@Param("memberId") int memberId, @Param("keyword") String keyword);

	// 댓글/대댓글 저장 (parentId 포함)
	void insertReply(BoardReplyDto replyDto);

	// 댓글 삭제
	void deleteReplyById(int replyId);

	// 댓글 작성자 확인
	int checkReplyWriter(@Param("replyId") int replyId, @Param("memberId") int memberId);

	// 댓글 좋아요(추천) 관련
	int checkReplyLike(@Param("replyId") int replyId, @Param("memberId") int memberId);

	// 마이페이지 - 게시물 관련 삭제
	int deleteMyPost(@Param("postId") int postId, @Param("memberId") int memberId);

	int deleteMyReply(@Param("replyId") int replyId, @Param("memberId") int memberId);

	int deleteMyLike(@Param("postId") int postId, @Param("memberId") int memberId);

	void insertReplyLike(@Param("replyId") int replyId, @Param("memberId") int memberId);

	void deleteReplyLike(@Param("replyId") int replyId, @Param("memberId") int memberId);

	int countReplyLikes(int replyId);

	// 댓글 수정

	int updateReply(@Param("replyId") int replyId, @Param("content") String content);

	// ===================== 신고 관련 =====================

	int checkReport(@Param("postId") int postId, @Param("memberId") int memberId);

	void insertReport(BoardReportDto reportDto);

	// ===================== 통계 및 마이페이지 요약 =====================

	int countMyPosts(@Param("writerId") int writerId);

	int countMyReplies(@Param("writerId") int writerId);

	// ⚠️ [삭제됨] selectTopRankers() 메서드는 RankingMapper.java로 이동함

	// ===================== 관리자 기능 =====================

	BoardDto selectPost(@Param("postId") int postId);

	// 🌟 [수정] 카테고리 ID와 삭제 여부 파라미터 추가
	List<BoardDto> adminPostList(@Param("keyword") String keyword, @Param("categoryId") Integer categoryId,
			@Param("deleted") Integer deleted, @Param("offset") int offset, @Param("size") int size);

	// 🌟 [수정] 전체 개수 조회에도 파라미터 추가
	int adminPostCount(@Param("keyword") String keyword, @Param("categoryId") Integer categoryId,
			@Param("deleted") Integer deleted);

	// 🌟 [추가] 게시글 상태 변경 (숨김/복구)
	void updateDeletedStatus(@Param("postId") int postId, @Param("status") int status);

	// 🌟 [추가] 관리자 검색용 카테고리 목록 조회
	List<Map<String, Object>> selectCategoryList();

	List<BoardReportDto> selectAllReports();

	int updateReportStatus(@Param("postId") int postId, @Param("memberId") int memberId, @Param("status") int status,
			@Param("adminMemo") String adminMemo);

	// 조회수 업데이트
	void updateHit(int postId);

}