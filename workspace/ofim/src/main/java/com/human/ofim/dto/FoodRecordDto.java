package com.human.ofim.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class FoodRecordDto {

	private long memberId;
	private long foodId;
	private String mealTime;
	private int healthRecordId;
	private String foodName;
	private int intake;
	private double intakeKcal;
	private double calories;
	private LocalDate recordDate;

}