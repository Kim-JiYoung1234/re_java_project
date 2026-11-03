package com.human.ofim.controller;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.human.ofim.dto.ExerciseRecordDto;
import com.human.ofim.dto.FoodRecordDto;
import com.human.ofim.dto.HealthInfoDto;
import com.human.ofim.dto.MemberDto;
import com.human.ofim.service.EnergyService;
import com.human.ofim.service.HealthService;

import jakarta.servlet.http.HttpSession;

@Controller
public class HealthController {

	private HealthService healthService;
	private EnergyService energyService;

	public HealthController(HealthService healthService, EnergyService energyService) {
		this.healthService = healthService;
		this.energyService = energyService;
	}

	@GetMapping("/record/myhealth")
	public String getMyHealth(HttpSession session, Model model, RedirectAttributes redirectAttributes) {

		MemberDto loginUser = (MemberDto) session.getAttribute("user");

		if (loginUser == null) {
			return "redirect:/home"; // 없으면 로그인 페이지로 보내버려
		}

		// 파트너/전문가는 들어오지마
		if ("파트너회원".equals(loginUser.getMemberType()) || "전문가회원".equals(loginUser.getMemberType())) {
			return "redirect:/home";
		}

		Long memberId = loginUser.getMemberId();

		// 3. 번호를 알았으니, 이제 건강 정보를 가져와!
		HealthInfoDto healthInfo = healthService.getMyHealthInfo(memberId);
		HealthInfoDto healthHabit = healthService.getMyHabit(memberId);
		// 나의 목표 가져오는 부분(칼로리때문에)
		HealthInfoDto energy = healthService.calculateKcal(healthInfo, session);

		List<FoodRecordDto> foodRecord = energyService.getFoodRecord(memberId);
		List<ExerciseRecordDto> exerciseRecord = energyService.getExerciseRecord(memberId);

		if (healthInfo == null) {
			healthInfo = new HealthInfoDto();
			healthInfo.setMemberId(memberId);
		}

		System.out.println("healthInfo: " + healthInfo);

		// 여기서 첫 번째 인자 "--"가 타임리프의 ${--}가 되는 거야!
		model.addAttribute("energy", energy);
		model.addAttribute("healthInfo", healthInfo);
		model.addAttribute("habit", healthHabit);
		model.addAttribute("user", loginUser); // 로그인한 사용자 정보도 넘겨주기
		model.addAttribute("foodRecords", foodRecord);
		model.addAttribute("exerciseRecords", exerciseRecord);

		return "record/myhealth";
	}

	@GetMapping("/record/myrecord")
	public String myRecordAndCreateChart(HttpSession session, Model model) {

		MemberDto loginUser = (MemberDto) session.getAttribute("user");

		if (loginUser == null) {
			return "redirect:/home"; // 없으면 로그인 페이지로 보내버려
		}

		// 파트너/전문가는 들어오지마
		if ("파트너회원".equals(loginUser.getMemberType()) || "전문가회원".equals(loginUser.getMemberType())) {
			return "redirect:/home";
		}

		// 차트 데이터 가져오는 부분
		// 1. 차트용 리스트 가져오기
		List<HealthInfoDto> chartData = healthService.getChartData(loginUser.getMemberId());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		// 2. 바구니 타입을 String으로
		List<String> recordDates = new ArrayList<>();
		List<Double> weights = new ArrayList<>();

		// 날짜를 "2026-02-22" 이런 형식으로
		// 1. 기존 sdf 대신 포맷터 생성(sdf는 Date타입 포맷)
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		for (HealthInfoDto dto : chartData) {

			if (dto.getWeight() > 0) {

				String formattedDate = "";
				if (dto.getRecordDate() != null) {
					formattedDate = dto.getRecordDate().format(formatter); // LocalDate의 format 메서드 사용!
				} else {
					formattedDate = LocalDate.now().format(formatter); // 날짜가 없으면 오늘 날짜로 방어 코드
				}

				recordDates.add(formattedDate);
				weights.add(dto.getWeight());
			}
		}

		Long memberId = loginUser.getMemberId();

		List<FoodRecordDto> foodRecords = energyService.getFoodRecordAll(memberId);
		List<ExerciseRecordDto> exerciseRecords = energyService.getExerciseRecordAll(memberId);

		List<HealthInfoDto> allWeights = healthService.getWeightData(memberId);
		List<HealthInfoDto> allHabit = healthService.getAllHabit(memberId);

		model.addAttribute("user", loginUser); // 로그인한 사용자 정보도 넘겨주기
		model.addAttribute("foodRecordAll", foodRecords);
		model.addAttribute("exerciseRecordAll", exerciseRecords);
		model.addAttribute("allWeights", allWeights);
		model.addAttribute("recordDates", recordDates);
		model.addAttribute("weights", weights);
		model.addAttribute("allHabits", allHabit);

		// 나의 목표 가져오는 부분
		HealthInfoDto healthPlan = healthService.getMyPlan(loginUser.getMemberId()); // 클래스말고 주입받은 생성자 사용
		// 여기서 첫 번째 인자 "00"가 타임리프의 ${00}가 되는 거
		model.addAttribute("healthPlan", healthPlan);

		return "record/myrecord";
	}

	// HTML의 action 주소와 이 주소가 일치해야 해!
	@PostMapping("/record/myhealth")
	public String saveHealthInfo(HealthInfoDto dto, HttpSession session) {

		try {
			// 여기서 서비스를 호출해 DB에 저장하는 로직
			if (healthService.hasInfo(dto.getMemberId())) {
				healthService.saveInfo(dto);
			} else {
				healthService.insertInfo(dto);
			}

		} catch (Exception e) {
			System.out.println("컨트롤러 에러 확인: " + e.getMessage());
			e.printStackTrace();
			return "redirect:/record/myhealth";
		}

		// 저장이 끝나면 다시 myhealth로 돌아가
		return "redirect:/record/myhealth";
	}

	@PostMapping("/record/myrecord")
	public String saveHealthPlan(HealthInfoDto dto, HttpSession session) {

		healthService.savePlan(dto, session);

		// 저장이 끝나면 다시 myrecord로 돌아가
		return "redirect:/record/myrecord";
	}

	@PostMapping("/record/myHabit")
	public String saveHealthHabit(HealthInfoDto dto, HttpSession session) {

		boolean hasHabit = healthService.hasHabit(dto.getMemberId());

		if (hasHabit) {
			System.out.println("습관 有 업뎃: " + dto);
			healthService.saveMyHabit(dto);
		} else {
			System.out.println("습관 無 인서트: " + dto);
			healthService.insertMyHabit(dto);
		}

		return "redirect:/record/myhealth";
	}

}