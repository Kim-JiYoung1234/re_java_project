package com.human.ofim.dto;

import java.time.LocalDate;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true) // 부모 필드까지 비교!
@Data
public class ExpertInfoDto extends ExpertUserDto {
	
	// 1. 자격증 뭉치 (기존 DTO를 리스트로 활용)
    // 주의: HTML의 name 속성과 매핑 로직을 맞추기 위해 
    // 필드별 List로 받는 것이 Spring에서 바인딩하기 가장 쉽습니다.
    private List<String> certName;
    private List<String> issuer;
    private List<String> certNo;
    private List<LocalDate> issuedDate;
    private List<LocalDate> expiredDate;
    private List<MultipartFile> certFile;

    // 2. 학력 뭉치
    private List<String> expertSchool;
    private List<String> expertMajor;
    private List<String> degreeName;
    private List<String> graduationYn;
    
    // 3. 경력 뭉치
    private List<String> organization;
    private List<String> careerMonth;
    private List<String> careerYear;
    private List<String> medicalDepartments;
    private List<String> isEmployed;

    // 4. 정산 정보 (단일)
    private String bankName;
    private String acctNo;

}
