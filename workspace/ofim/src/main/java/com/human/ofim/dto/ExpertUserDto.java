package com.human.ofim.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ExpertUserDto extends MemberDto {

    private String specialty;
    private String expertIntroduce;
    private Boolean isOnline;
    private Integer approveStatus;
    private LocalDateTime approvedDate;

    // 관리자 목록 통계
    private Long totalConsultCount;
    private Long totalMinutes;
    
    // 추천 점수
    private Double avgRating;
    private Integer reviewCount;

    // 전문가 전용 프로필 이미지
    private String profileImagePath;
    private MultipartFile profileImageFile;
    
    // 운영용 추가 필드
    private String statusReason;
    
}