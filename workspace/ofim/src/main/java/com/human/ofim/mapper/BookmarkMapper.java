package com.human.ofim.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

// 🌟 중요: 형의 프로젝트에 있는 Board 관련 DTO를 임포트해줘! 
// (패키지명이 다르면 수정해줘, 예: com.human.ofim.dto.BoardDto)
import com.human.ofim.dto.BoardDto;

@Mapper
public interface BookmarkMapper {

	// 1. 북마크 여부 확인 (memberId 타입을 int으로 변경!)
	int checkBookmark(@Param("memberId") Long memberId, @Param("postId") int postId);

	// 2. 북마크 추가 (memberId 타입을 int으로 변경!)
	void insertBookmark(@Param("memberId") Long memberId, @Param("postId") int postId);

	// 3. 북마크 삭제 (memberId 타입을 int으로 변경!)
	void deleteBookmark(@Param("memberId") Long memberId, @Param("postId") int postId);

	// 4. 내 북마크 목록 가져오기 (리턴 타입을 BoardDto로 맞춤)
	List<BoardDto> getMyBookmarks(@Param("memberId") Long memberId);
	
	List<BoardDto> getMyBookmarksPaging(@Param("memberId") int memberId, @Param("keyword") String keyword, @Param("offset") int offset, @Param("amount") int amount);

	int countMyBookmarks(@Param("memberId") int memberId, @Param("keyword") String keyword);
}