package com.human.ofim.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ExerciseDetailDto {

	private int exerciseId;
	private String exerciseName;
	private String exerciseUnit;
	private String intensity;
	private BigDecimal calories;
	
	//화면에 보여주려고 매퍼에서 만듦
	private int displayTime;
	private String displayUnit;
}