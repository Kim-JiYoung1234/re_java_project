package com.human.ofim.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.web.multipart.MultipartFile;
import lombok.Data;

@Data
public class MemberDto {

	private Long memberId; // 회원 번호 (PK)
	private String userName; // 이름
	private String nickname; // 닉네임
	private String loginId; // 로그인 아이디
	private String email; // 이메일
	private String password; // 비밀번호
	private String zipcode; // 우편번호
	private String address; // 주소
	private String address2; // 상세 주소
	private String phone; // 전화번호
	private String gender; // 성별
	private LocalDate birthDate; // 생년월일

	private String memberType; // 회원 유형
	private String memberStatus;
	private String gradeName;
	private Integer gradeId;
	private int activityPoint; // 활동 점수 추가

	private LocalDateTime createdDate; // 가입 날짜
	private LocalDateTime updatedDate; // 정보 수정 날짜
	private LocalDateTime lastLoginDate; // 마지막 로그인 날짜

	// 공통 프로필 정보
	private String profile;
	private MultipartFile profileFile;

	/*
	 * * [수정] 자식 DTO(PartnerUserDto)와 변수명이 겹치지 않도록 파트너 전용 필드는 상속받는 클래스에서 관리합니다.
	 */
}
