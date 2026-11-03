package com.human.ofim.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.human.ofim.dto.ExerciseDetailDto;
import com.human.ofim.dto.ExerciseRecordDto;
import com.human.ofim.dto.FoodDetailDto;
import com.human.ofim.dto.FoodRecordDto;
import com.human.ofim.mapper.EnergyMapper;

@Service(value = "energyService")
public class EnergyServiceImpl implements EnergyService {

	private EnergyMapper energyMapper;

	public EnergyServiceImpl(EnergyMapper energyMapper) {
		this.energyMapper = energyMapper;
	}

	public List<FoodDetailDto> searchFoodByName(String foodKeyword) {
		if (foodKeyword == null || foodKeyword.trim().isEmpty()) {
			System.out.println("----------------------키워드 없음---------------");
		} else {
			System.out.println("----------------------키워드 있음---------------");
		}

		return energyMapper.selectFoodByName(foodKeyword);
	}

	@Override
	public List<ExerciseDetailDto> searchExerciseByName(String exerciseKeyword) {

		if (exerciseKeyword == null || exerciseKeyword.trim().isEmpty()) {
			System.out.println("----------------------키워드 없음---------------");
		} else {
			System.out.println("----------------------키워드 있음---------------");
		}
		return energyMapper.selectExerciseByName(exerciseKeyword);
	}

	@Override
	public List<FoodDetailDto> getAllFood() {

		return energyMapper.selectAllFood();
	}

	@Override
	public List<ExerciseDetailDto> getAllExercise() {

		return energyMapper.selectAllExercise();
	}

	@Override
	public List<FoodRecordDto> getFoodRecord(Long memberId) {

		List<FoodRecordDto> dtos = energyMapper.selectFoodRecord(memberId);

		for (FoodRecordDto dto : dtos) {
			double kcal = (double) dto.getIntake() * dto.getCalories() / 100;
			dto.setIntakeKcal(kcal);
		}
		return dtos;
	}

	@Override
	public List<ExerciseRecordDto> getExerciseRecord(Long memberId) {

		List<ExerciseRecordDto> dtos = energyMapper.selectExerciseRecord(memberId);

		double kcal = 0.0;
		for (ExerciseRecordDto dto : dtos) {
			if (dto.getExerciseUnit().equals("REP")) {
				kcal = (double) dto.getVolume() * dto.getCalories();
				dto.setBurnKcal(kcal);
			} else {
				kcal = (double) dto.getVolume() * dto.getCalories() / 60;
				dto.setBurnKcal(kcal);
			}

		}
		return dtos;
	}

	@Override
	public void saveFood(FoodRecordDto foodRecord) {

		// 헬스인포에 값없는데 먼저 먹은거부터 넣고싶을때
		energyMapper.insertEmptyHealthInfoToday(foodRecord.getMemberId());
		
		energyMapper.insertFoodData(foodRecord);
	}

	@Override
	public void saveExercise(ExerciseRecordDto exerciseRecord) {

		// 헬스인포에 값없는데 먼저 운동부터 넣고싶을때
		energyMapper.insertEmptyHealthInfoToday(exerciseRecord.getMemberId());

		energyMapper.insertExerciseData(exerciseRecord);
	}

	@Override
	public List<FoodRecordDto> getFoodRecordAll(Long memberId) {
		List<FoodRecordDto> dtos = energyMapper.selectFoodRecordAll(memberId);

		for (FoodRecordDto dto : dtos) {
			double kcal = (double) dto.getIntake() * dto.getCalories() / 100;
			dto.setIntakeKcal(kcal);
		}
		return dtos;
	}

	@Override
	public List<ExerciseRecordDto> getExerciseRecordAll(Long memberId) {
		List<ExerciseRecordDto> dtos = energyMapper.selectExerciseRecordAll(memberId);

		double kcal = 0.0;
		for (ExerciseRecordDto dto : dtos) {
			if (dto.getExerciseUnit().equals("REP")) {
				kcal = (double) dto.getVolume() * dto.getCalories();
				dto.setBurnKcal(kcal);
			} else {
				kcal = (double) dto.getVolume() * dto.getCalories() / 60;
				dto.setBurnKcal(kcal);
			}

		}
		return dtos;
	}

}