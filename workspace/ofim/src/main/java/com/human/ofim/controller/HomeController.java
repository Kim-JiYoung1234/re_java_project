package com.human.ofim.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.human.ofim.dto.BoardDto;
import com.human.ofim.service.BoardService;
import com.human.ofim.service.FitnessService;
import com.human.ofim.service.HealthService;
import com.human.ofim.service.RankingService; // 🌟 추가됨

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor // 🌟 생성자 노가다 대신 이거 하나면 주입 끝!
public class HomeController {

	private final HealthService healthService;
	private final FitnessService fitnessService;
	private final BoardService boardService;
	private final RankingService rankingService; // 🌟 실시간 랭킹을 위해 추가!

	// 1. 랜딩 페이지 (루트 경로)
	// 보통 메인 홈과 랜딩을 구분하려면 주소를 다르게 쓰거나, 하나로 합쳐야 해.
	// 형이 준 코드대로 "/"는 랜딩으로 유지할게.
	@GetMapping("/")
	public String landing(Model model) {
		model.addAttribute("bannerList", fitnessService.getPremiumBanners());
		return "landing";
	}

	// 2. 홈 화면 (공지사항 + 실시간 랭킹 통합!)
	@GetMapping("/home")
	public String home(Model model) {
		// [1] 배너 데이터 전달
		model.addAttribute("bannerList", fitnessService.getPremiumBanners());

		// [2] 최신 공지사항 5개 가져오기 (카테고리 ID: 1)
		List<BoardDto> recentNotices = boardService.findAll(1, null, null, 1, "createdDate_desc");
		if (recentNotices != null && recentNotices.size() > 5) {
			recentNotices = recentNotices.subList(0, 5);
		}
		model.addAttribute("recentNotices", recentNotices);

		// [3] 🌟 실시간 랭킹 TOP 3 데이터 가져오기 (커뮤니티와 동일 로직)
		List<BoardDto> topRankers = rankingService.getTopRankers();
		model.addAttribute("topRankers", topRankers != null ? topRankers : new ArrayList<>());

		return "home";
	}

	@GetMapping("/membership")
	public String membership() {
		return "membership";
	}
}