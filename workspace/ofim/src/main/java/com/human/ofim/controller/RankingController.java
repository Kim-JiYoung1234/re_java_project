package com.human.ofim.controller;

import com.human.ofim.dto.BoardDto;
import com.human.ofim.service.RankingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody; // 👈 이거 꼭 있어야 함!
import java.util.List;

@Controller
@RequestMapping("/ranking")
public class RankingController {

	@Autowired
	private RankingService rankingService;

	// 1. 기존 페이지 이동 (껍데기만 띄움)
	@GetMapping("/main")
	public String rankingMain(Model model) {
		// 기존처럼 데이터를 보내도 되지만, AJAX로 할 거면 사실 안 보내도 무방해.
		// 하지만 혹시 모르니 그대로 유지할게!
		List<BoardDto> rankers = rankingService.getAllRankers();
		model.addAttribute("rankers", rankers);
		return "community/ranking";
	}

	// 2. 자바스크립트(AJAX)가 호출할 데이터 API
	@GetMapping("/api/rankers")
	@ResponseBody // 🌟 중요: HTML을 찾지 않고 순수 JSON 데이터만 반환함
	public List<BoardDto> getRankersApi() {
		return rankingService.getAllRankers();
	}
}