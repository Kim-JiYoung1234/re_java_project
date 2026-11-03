package com.human.ofim.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.human.ofim.dto.BoardDto;
import com.human.ofim.mapper.BookmarkMapper;

@Service
public class BookmarkService {

	@Autowired
	private BookmarkMapper bookmarkMapper;

	/**
	 * 북마크 토글 기능 * @param memberId (협업 규칙에 따라 대문자 Long으로 통일!)
	 * 
	 * @param postId
	 * @return 추가되면 true, 삭제되면 false
	 */
	public boolean toggleBookmark(Long memberId, int postId) { 
		// 1. 이미 북마크가 존재하는지 확인
		if (bookmarkMapper.checkBookmark(memberId, postId) > 0) {
			// 존재하면 삭제
			bookmarkMapper.deleteBookmark(memberId, postId);
			return false; // 북마크 취소됨
		} else {
			// 존재하지 않으면 추가
			bookmarkMapper.insertBookmark(memberId, postId);
			return true; // 북마크 추가됨
		}
	}
	
	public List<BoardDto> getMyBookmarksPaging(int memberId, String keyword, int page, int amount) {
        int offset = (page - 1) * amount; // 페이지 번호를 DB가 아는 offset으로 변환!
        return bookmarkMapper.getMyBookmarksPaging(memberId, keyword, offset, amount);
    }

    public int countMyBookmarks(int memberId, String keyword) {
        return bookmarkMapper.countMyBookmarks(memberId, keyword);
    }
}