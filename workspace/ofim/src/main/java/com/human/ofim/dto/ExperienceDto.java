package com.human.ofim.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true) // 부모 필드까지 비교!
@Data
public class ExperienceDto extends ExpertUserDto {
	
	private Long careerId;
	private String organization;
	private String careerMonth;
	private String careerYear;
	private String medicalDepartments;
	private String isEmployed;

}
