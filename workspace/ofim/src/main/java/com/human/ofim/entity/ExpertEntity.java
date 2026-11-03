package com.human.ofim.entity;

import com.human.ofim.dto.MemberDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "expert")
public class ExpertEntity {
	
	public ExpertEntity() {
		// 기본 생성자
	}
	
	@Id
	@Column(name = "member_id")
	private Integer memberId;
	
    private String specialty;
    private String education;
    private String career;
	
	/*
	 * public ExpertEntity(Integer memberId, MemberDto memberDto) {
	 * 
	 * this.memberId = memberId; this.specialty = memberDto.getSpecialty();
	 * this.education = memberDto.getEducation(); this.career =
	 * memberDto.getCareer(); }
	 */
	
	

}
