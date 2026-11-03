package com.human.ofim.dto;

import lombok.Data;

@Data
public class FoodDetailDto {
	
	private int foodId;
	private String foodName;
	private double kCal;
	private double carbs;
	private double protein;
	private double fat;
	private double sugar;
	private double sodum;
	private double transFat;
	private double saturatedFat;
	private double cholesterol;
	private int countUnit;
}
