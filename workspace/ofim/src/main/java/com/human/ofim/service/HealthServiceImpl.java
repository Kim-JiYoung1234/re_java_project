package com.human.ofim.service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

import org.springframework.stereotype.Service;

import com.human.ofim.dto.ExerciseRecordDto;

import javax.sql.DataSource;
import com.human.ofim.dto.FoodRecordDto;
import com.human.ofim.dto.HealthInfoDto;
import com.human.ofim.dto.MemberDto;
import com.human.ofim.mapper.EnergyMapper;
import com.human.ofim.mapper.RecordMapper;

import jakarta.servlet.http.HttpSession;

@Service(value = "healthService")
public class HealthServiceImpl implements HealthService {

	private final DataSource dataSource;

	private RecordMapper recordMapper;
	private EnergyMapper energyMapper;

	public HealthServiceImpl(RecordMapper recordMapper, EnergyMapper energyMapper, DataSource dataSource) {
		this.recordMapper = recordMapper;
		this.energyMapper = energyMapper;
		this.dataSource = dataSource;
	}

	@Override
	public void insertInfo(HealthInfoDto healthInfo) {

		try {
			// 값 없으면 오늘자로 넣어줘
			if (healthInfo.getRecordDate() == null) {
				healthInfo.setRecordDate(LocalDate.now());
			}
			// 오늘자 값만 받아줘
			if (healthInfo.getRecordDate().compareTo(LocalDate.now()) == 0) {
				// 매퍼에게 받은 짐(dto)을 DB에 잘 넣어달라고 시키는 거야!
				recordMapper.insertHealthInfo(healthInfo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void saveInfo(HealthInfoDto healthInfo) {

		try {
			// 값 없으면 오늘자로 넣어줘
			if (healthInfo.getRecordDate() == null) {
				healthInfo.setRecordDate(LocalDate.now());
			}
			// 오늘자 값만 받아줘
			if (healthInfo.getRecordDate().compareTo(LocalDate.now()) == 0) {
				// 매퍼에게 받은 짐(dto)을 DB에 잘 넣어달라고 시키는 거야!
				recordMapper.updateHealthInfo(healthInfo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public HealthInfoDto getMyHealthInfo(Long memberId) {

		HealthInfoDto userInfo;
		if (memberId == 0) {
			return null;
		} else {
			userInfo = recordMapper.selectHealthInfoByDate(memberId);
		}

		return userInfo;
	}

//	@Override
//	public void insertPlan(HealthInfoDto healthInfo, HttpSession session) {
//		calculateKcal(healthInfo, session);
//	}

	@Override
	public void savePlan(HealthInfoDto healthInfo, HttpSession session) {
		calculateKcal(healthInfo, session);

		if (hasPlan(healthInfo.getMemberId())) {
			recordMapper.updateUserPlan(healthInfo);
		} else {
			recordMapper.insertUserPlan(healthInfo);
		}
	}

	@Override
	public HealthInfoDto getMyPlan(Long memberId) {

		HealthInfoDto userPlan;
		userPlan = recordMapper.selectUserPlan(memberId);

		return userPlan;

	}

	@Override
	public HealthInfoDto calculateKcal(HealthInfoDto healthInfo, HttpSession session) {

		// 로그인한 사용자의 생년월일을 가져오기 위해 세션에서 사용자 정보를 조회
		MemberDto loginUser = (MemberDto) session.getAttribute("user");

		if (healthInfo == null) {
			System.out.println("건강 정보 데이터가 없어서 계산을 중단합니다.");
			return null; // 데이터가 없으면 더 이상 진행하지 않고 메서드 종료!
		}

		healthInfo.setBirthDate(loginUser.getBirthDate());
		healthInfo.setGender(loginUser.getGender());

		// 1. 생년월일로 만 나이 계산하기
		// String 타입의 생일을 LocalDate로 변환하기
		LocalDate birthDate = loginUser.getBirthDate();

		int age = Period.between(birthDate, LocalDate.now()).getYears();

		System.out.println("나이: " + age);

		double s;
		double activityFactor;

		if (healthInfo.getGender().equalsIgnoreCase("M")) {
			s = 5;
		} else {
			s = -161;
		}

		// 키랑 기간이랑 목표체중이 왜 안나오는지 모르겟는데 일단 따로가져오기
		Double userHeight = 0.0;
		Double userGoalWeight = 0.0;
		Integer userDietPeriod = 0;

		if (healthInfo.getHeight() == null || healthInfo.getHeight() <= 0.0) {
			Double dbUserHeight = recordMapper.getHeightById(loginUser.getMemberId());
			if (dbUserHeight == null) {
				userHeight = 0.0;
			} else {
				userHeight = recordMapper.getHeightById(loginUser.getMemberId());
			}
			System.out.println("userHeight: " + userHeight);
		} else {
			userHeight = healthInfo.getHeight();
			System.out.println("userHeight: " + userHeight);

		}
		if (healthInfo.getGoalWeight() == null || healthInfo.getGoalWeight() <= 0.0) {
			Double dbUserGoalWeight = recordMapper.getGoalWeightById(loginUser.getMemberId());
			if (dbUserGoalWeight == null) {
				userGoalWeight = 0.0;
			} else {
				userGoalWeight = recordMapper.getGoalWeightById(loginUser.getMemberId());
			}
			System.out.println("userGoalWeight: " + userGoalWeight);

		} else {
			userGoalWeight = healthInfo.getGoalWeight();
			System.out.println("userGoalWeight: " + userGoalWeight);
		}

		if (healthInfo.getDietPeriod() == null || healthInfo.getDietPeriod() <= 0.0) {
			Integer dbUserDietPeriod = recordMapper.getDietPeriodById(loginUser.getMemberId());
			if (dbUserDietPeriod == null) {
				userDietPeriod = 0;
			} else {
				userDietPeriod = recordMapper.getDietPeriodById(loginUser.getMemberId());
			}
			System.out.println("userDietPeriod: " + userDietPeriod);
		} else {
			userDietPeriod = healthInfo.getDietPeriod();
			System.out.println("userDietPeriod: " + userDietPeriod);
		}

		// 2. 기초대사량(BMR) 계산 (나이 변수 'age' 사용)
		double bmr = (10 * healthInfo.getWeight()) + (6.25 * userHeight) - (5 * age) // 여기서 계산한 나이를
				+ s;

		// 2. 활동대사량(TDEE) 계산
		if (healthInfo.getBaseActivityLevel() == 0) {
			activityFactor = 1.2; // 기본값으로 비활동적 수준 적용
		} else {
			activityFactor = healthInfo.getBaseActivityLevel(); // 사용자가 입력한 활동 수준 적용
		}

		double tdee = bmr * activityFactor;

		// 3. 목표 감량을 위한 하루 필요 에너지 결손량(Deficit) 계산
		// (현재체중 - 목표체중) * 7700 / 다이어트 기간
		double totalWeightToLose = healthInfo.getWeight() - userGoalWeight;
		double totalCaloriesToBurn = totalWeightToLose * 7700;
		double dailyDeficit = totalCaloriesToBurn / (userDietPeriod * 30.0);

		// 3. 결과 도출
		// 권장 섭취 = 평소 소모량(TDEE) - (하루 필요 결손량(dailyDeficit)의 절반은 식단으로!)
		// 권장 소모 = (하루 필요 결손량의 나머지 절반은 운동으로!)

		int calculateIntake = (int) Math.round(tdee - (dailyDeficit * 0.5));
		int calculateBurn = (int) Math.round(dailyDeficit * 0.5);

		// 기초대사량만큼은 먹자
		int minIntake = (int) Math.round(bmr);
		if (calculateIntake < minIntake) {
			healthInfo.setDailyIntakeKcal(minIntake);

			// 대신 그만큼 움직여
			int encouragedBurn = (int) Math.round(tdee + dailyDeficit - minIntake);
			healthInfo.setDailyBurnKcal(encouragedBurn);
		} else {
			healthInfo.setDailyIntakeKcal(calculateIntake);
			healthInfo.setDailyBurnKcal(calculateBurn);
		}

		// 계산된 값을 객체에 담아서 반환
		return healthInfo;
	}

	// 차트 데이터 조회
	@Override
	public List<HealthInfoDto> getChartData(Long memberId) {

		return recordMapper.selectChartData(memberId);
	}

	@Override
	public List<HealthInfoDto> getWeightData(Long memberId) {
		return recordMapper.getAllWeight(memberId);
	}

	public Integer getMemberIdByLoginId(String loginId) {
		// 매퍼한테 "이 로그인 아이디(이메일 등) 쓰는 사람 번호가 뭐야?" 라고 물어보는 거야.
		return recordMapper.findMemberIdByLoginId(loginId);
	}

	@Override
	public boolean hasInfo(Long memberId) {
		int userInfo = recordMapper.hasInfo(memberId);

		if (userInfo == 1) {
			return true; // 계획이 있으면 true 반환
		}
		return false;
	}

	@Override
	public boolean hasPlan(Long memberId) {

		int userPlan = recordMapper.hasPlan(memberId);

		if (userPlan == 1) {
			return true; // 계획이 있으면 true 반환
		}
		return false;
	}

	@Override
	public boolean hasHabit(Long memberId) {

		int hasInfo = recordMapper.hasInfo(memberId);

		if (hasInfo == 1) {
			return true; // 계획이 있으면 true 반환
		} else {
			return false;
		}
	}

	public void insertMyHabit(HealthInfoDto healthInfo) {
		System.out.println("전송 직전 데이터 확인: " + healthInfo.toString());
		recordMapper.insertUserHabit(healthInfo);
	}

	public void saveMyHabit(HealthInfoDto healthInfo) {
		System.out.println("전송 직전 데이터 확인: " + healthInfo.toString());
		recordMapper.updateUserHabit(healthInfo);
	}

	@Override
	public HealthInfoDto getMyHabit(Long memberId) {

		return recordMapper.selectUserHabit(memberId);
	}

	@Override
	public List<HealthInfoDto> getAllHabit(Long memberId) {

		return recordMapper.selectAllHabit(memberId);

	}

}
