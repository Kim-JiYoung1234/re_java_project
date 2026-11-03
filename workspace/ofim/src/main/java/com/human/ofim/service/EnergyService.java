package com.human.ofim.service;

import java.util.List;

import com.human.ofim.dto.ExerciseDetailDto;
import com.human.ofim.dto.ExerciseRecordDto;
import com.human.ofim.dto.FoodDetailDto;
import com.human.ofim.dto.FoodRecordDto;

public interface EnergyService {
	public List<FoodDetailDto> searchFoodByName(String foodKeyword);
	public List<ExerciseDetailDto> searchExerciseByName(String exerciseKeyword);
	public List<FoodDetailDto> getAllFood();
	public List<ExerciseDetailDto> getAllExercise();

	public void saveFood(FoodRecordDto foodRecord);
	public void saveExercise(ExerciseRecordDto exerciseRecord);
	
	public List<FoodRecordDto> getFoodRecord(Long memberId);
	List<ExerciseRecordDto> getExerciseRecord(Long memberId);

	public List<FoodRecordDto> getFoodRecordAll(Long memberId);
	List<ExerciseRecordDto> getExerciseRecordAll(Long memberId);


}
