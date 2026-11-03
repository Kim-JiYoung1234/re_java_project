package com.human.ofim.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true) // 부모 필드까지 비교!
@Data
public class EducationDto extends ExpertUserDto {
	
	private Long careerId;
	private String expertSchool;
	private String expertMajor;
	private String degreeName;
	private String graduationYn;

}
