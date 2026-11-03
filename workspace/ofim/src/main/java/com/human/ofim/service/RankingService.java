package com.human.ofim.service;

import com.human.ofim.dto.BoardDto;
import com.human.ofim.mapper.RankingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RankingService {

	@Autowired
	private RankingMapper rankingMapper;

	// 전체 랭킹 리스트 가져오기
	public List<BoardDto> getAllRankers() {
		return rankingMapper.selectAllRankers();
	}

	// 사이드바용 Top 5 가져오기
	public List<BoardDto> getTopRankers() {
		return rankingMapper.selectTopRankers();
	}

	// 🌟 [추가] 로그인한 사용자의 현재 순위 가져오기
	public int getMemberRank(int memberId) {
		// 1. 전체 랭킹 리스트를 가져온다 (포인트 높은 순)
		List<BoardDto> allRankers = rankingMapper.selectAllRankers();

		if (allRankers == null || allRankers.isEmpty()) {
			return 0;
		}

		// 2. 리스트를 돌면서 내 memberId와 일치하는 순번을 찾는다
		for (int i = 0; i < allRankers.size(); i++) {
			// BoardDto에 저장된 작성자 ID 필드명을 확인해줘 (여기서는 writerId라고 가정)
			if (allRankers.get(i).getWriterId() == memberId) {
				return i + 1; // 인덱스는 0부터 시작하니까 +1이 실제 등수!
			}
		}

		// 3. 순위권에 없으면 0 반환
		return 0;
	}
}