package com.human.ofim.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class ExerciseRecordDto {

	private Long memberId;
	private int exerciseId;
	private int healthRecordId;
	private String exerciseName;
	private String exerciseUnit;
	private int volume;
	private double burnKcal;
	private double calories;
	private LocalDate recordDate;
}