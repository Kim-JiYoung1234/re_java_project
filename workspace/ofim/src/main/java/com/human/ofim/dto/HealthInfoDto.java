package com.human.ofim.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class HealthInfoDto extends MemberDto {
	
	private long healthRecordId;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate recordDate;
	private double weight;
	private double bmi;
	private double fat;
	private double muscle;
	
	private Double height;
	private Double goalWeight;
	private Integer dietPeriod;
	
	private int dailyIntakeKcal;
	private int dailyBurnKcal;
	private int baseActivityLevel;
	
	private LocalDate birthDate;
	private String gender;
	
	private Integer sleepHour;
	
	private Integer waterIntake;
	
	private Integer bowelCount;
}