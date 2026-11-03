package com.human.ofim.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.human.ofim.dto.ExerciseDetailDto;
import com.human.ofim.dto.ExerciseRecordDto;
import com.human.ofim.dto.FoodDetailDto;
import com.human.ofim.dto.FoodRecordDto;
import com.human.ofim.dto.MemberDto;
import com.human.ofim.service.EnergyService;

import jakarta.servlet.http.HttpSession;

@Controller
public class EnergyController {

	private EnergyService energyService;

	public EnergyController(EnergyService energyService) {
		this.energyService = energyService;
	}

	@GetMapping("/record/foodSearch")
	public String foodSearch(Model model, HttpSession session,
			@RequestParam(value = "foodKeyword", required = false) String foodKeyword) {
		
		List<FoodDetailDto> dtos;
		
		if (foodKeyword == null || foodKeyword.isEmpty()) {
			dtos = energyService.getAllFood();
			System.out.println("전체 조회된 음식 개수: " + (dtos != null ? dtos.size() : "null"));
		} else {
			dtos = energyService.searchFoodByName(foodKeyword);
			System.out.println(dtos.toString());
		}
		model.addAttribute("foods", dtos);
		return "record/foodSearch";
	}

	@GetMapping("/record/exerciseSearch")
	public String exerciseSearch(Model model, HttpSession session,
			@RequestParam(value = "exerciseKeyword", required = false) String exerciseKeyword) {
//		
//		MemberDto loginUser = (MemberDto) session.getAttribute("user");
//		if(loginUser == null)

		List<ExerciseDetailDto> dtos;

		if (exerciseKeyword == null || exerciseKeyword.isEmpty()) {
			dtos = energyService.getAllExercise();
		} else {
			dtos = energyService.searchExerciseByName(exerciseKeyword);
			System.out.println(dtos.toString());
		}
		model.addAttribute("exercises", dtos);
		return "record/exerciseSearch";
	}

	@PostMapping("/record/foodSearch-data")
	public String foodDataSave(FoodRecordDto dto, HttpSession session) {

		MemberDto loginUser = (MemberDto) session.getAttribute("user");
		dto.setMemberId(loginUser.getMemberId());

		energyService.saveFood(dto);

		System.out.println("foodDataSave: " + dto);

		return "redirect:/record/myhealth";
	}

	@PostMapping("/record/exerciseSearch-data")
	public String exerciseDataSave(ExerciseRecordDto dto, HttpSession session) {

		MemberDto loginUser = (MemberDto) session.getAttribute("user");
		dto.setMemberId(loginUser.getMemberId());

		energyService.saveExercise(dto);

		System.out.println("exerciseDataSave: " + dto);

		return "redirect:/record/myhealth";
	}

}
