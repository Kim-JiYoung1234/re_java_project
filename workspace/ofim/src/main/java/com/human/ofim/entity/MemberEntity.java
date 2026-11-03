package com.human.ofim.entity;

import java.time.LocalDate;

import com.human.ofim.dto.MemberDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "user")
public class MemberEntity {
	
	public MemberEntity() {
		// 기본 생성자
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT
	private Integer memberId;
	
	@Column(name = "login_id", unique = true, nullable = false)
	private String loginId;
	@Column(name = "user_name", nullable = false)
	private String userName;
	@Column(name = "nickname", nullable = false)
	private String nickName;
	private String email;
	private String password;
	private String address;
	private String address2;
	private String phone;
	private String gender;
	@Column(name = "birth_date")
	private LocalDate birthDate;
	@Column(name = "member_type", nullable = false)
	private String memberType;
	
	public MemberEntity(MemberDto memberDto) {
		this.userName = memberDto.getUserName();
		//this.nickName = memberDto.getNickname();
		this.loginId = memberDto.getLoginId();
		this.email = memberDto.getEmail();
		this.password = memberDto.getPassword();
		this.address = memberDto.getAddress();
		this.address2 = memberDto.getAddress2();
		this.phone = memberDto.getPhone();
		this.gender = memberDto.getGender();
		this.birthDate = memberDto.getBirthDate();
		this.memberType = memberDto.getMemberType();
	}
	
	

}
