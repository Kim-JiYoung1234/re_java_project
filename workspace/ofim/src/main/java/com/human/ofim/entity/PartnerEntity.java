package com.human.ofim.entity;

import com.human.ofim.dto.MemberDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "partner")
public class PartnerEntity {
	
	public PartnerEntity() {
		// 기본 생성자
	}
	
	@Id
	@Column(name = "member_id")
	private Integer memberId;
	@Column(name = "type", nullable = false)
    private String partnerType;
	@Column(name = "business_no", unique = true, nullable = false)
    private String businessNo;
	@Column(name = "company_name", nullable = false)
    private String companyName;
	@Column(name = "homepage_url")
    private String homePage;
	@Column(name = "facility_amenities")
    private String facility;
    private String description;
    @Column(name = "oper_time")
    private String operTime;
	
	public PartnerEntity(Integer memberId, MemberDto memberDto) {
		
		/*
		 * this.memberId = memberId; this.partnerType = memberDto.getPartnerType();
		 * this.businessNo = memberDto.getBusinessNo(); this.companyName =
		 * memberDto.getCompanyName(); this.homePage = memberDto.getHomePage();
		 * this.facility = memberDto.getFacility(); this.description =
		 * memberDto.getDescription(); this.operTime = memberDto.getOperTime();
		 */
	}
	
	

}
