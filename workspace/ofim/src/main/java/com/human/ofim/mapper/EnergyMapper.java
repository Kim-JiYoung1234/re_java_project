package com.human.ofim.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.human.ofim.dto.ExerciseDetailDto;
import com.human.ofim.dto.ExerciseRecordDto;
import com.human.ofim.dto.FoodDetailDto;
import com.human.ofim.dto.FoodRecordDto;

@Mapper
public interface EnergyMapper {

	List<FoodDetailDto> selectFoodByName(String foodKeyword);
	List<ExerciseDetailDto> selectExerciseByName(String exerciseKeyword);
	List<FoodDetailDto> selectAllFood();
	List<ExerciseDetailDto> selectAllExercise();
	
	void insertFoodData(FoodRecordDto foodRecord);
	void insertExerciseData(ExerciseRecordDto exerciseRecord);
	
	List<FoodRecordDto> selectFoodRecord(long memberId);
	List<ExerciseRecordDto> selectExerciseRecord(long memberId);
	void insertEmptyHealthInfoToday(long memberId);
	
	List<FoodRecordDto> selectFoodRecordAll(long memberId);
	List<ExerciseRecordDto> selectExerciseRecordAll(long memberId);
	
}
