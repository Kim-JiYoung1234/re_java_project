package com.human.ofim.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.human.ofim.dto.ExerciseRecordDto;
import com.human.ofim.dto.FoodRecordDto;
import com.human.ofim.dto.HealthInfoDto;

@Mapper
public interface RecordMapper {

	HealthInfoDto selectHealthInfoByDate(Long memberId);
	void insertHealthInfo(HealthInfoDto healthInfo);
	void updateHealthInfo(HealthInfoDto healthInfo);
	
	void insertUserPlan(HealthInfoDto healthInfo);
	void updateUserPlan(HealthInfoDto healthInfo);
	HealthInfoDto selectUserPlan(Long memberId);
	
	List<HealthInfoDto> selectChartData(Long memberId);
	
	int findMemberIdByLoginId(String loginId);
	Double getHeightById(Long memberId);
	Double getGoalWeightById(Long memberId);
	Integer getDietPeriodById(Long memberId);
	
	void insertUserHabit(HealthInfoDto healthInfo);
	void updateUserHabit(HealthInfoDto healthInfo);
	HealthInfoDto selectUserHabit(Long memberId);

	List<HealthInfoDto> getAllWeight(Long memberId);
	int hasInfo(Long memberId);
	int hasPlan(Long memberId);
	List<HealthInfoDto> selectAllHabit(Long memberId);
}
