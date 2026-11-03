package com.human.ofim.service;

import java.util.List;

import com.human.ofim.dto.HealthInfoDto;

import jakarta.servlet.http.HttpSession;

public interface HealthService {

	public HealthInfoDto getMyHealthInfo(Long memberId);

	public void insertInfo(HealthInfoDto healthInfo);

	public void saveInfo(HealthInfoDto healthInfo);
	
	public boolean hasInfo(Long memberId);

//	public void insertPlan(HealthInfoDto healthInfo, HttpSession session);

	public void savePlan(HealthInfoDto healthInfo, HttpSession session);

	public boolean hasPlan(Long memberId);

	public HealthInfoDto getMyPlan(Long memberId);

	public HealthInfoDto calculateKcal(HealthInfoDto dto, HttpSession session);

	public List<HealthInfoDto> getChartData(Long memberId);
	
	public List<HealthInfoDto> getWeightData(Long memberId);

	public Integer getMemberIdByLoginId(String loginId);
	
	public void insertMyHabit(HealthInfoDto healthInfo);
	
	public void saveMyHabit(HealthInfoDto healthInfo);

	public HealthInfoDto getMyHabit(Long memberId);

	public boolean hasHabit(Long memberId);

	public List<HealthInfoDto> getAllHabit(Long memberId);

}
