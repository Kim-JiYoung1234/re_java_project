package com.human.ofim.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class ConsultDto {

    private Long consultId;
    private Long memberId;
    private Long expertId;

    private LocalDateTime consultStartDate;
    private LocalDateTime consultEndDate;

    private String consultType;
    private Integer status;
    private String statusText;

    private LocalDateTime createdDate;
    private String contactPhone;

    private String title;
    private String consultContent;
    private String answerContent;

    private String memberName;
    private String expertName;

    private String clientLoginId;
    private String clientName;
    private String clientEmail;

    private Integer minutes;
    private String answerStatus;
    private String delayStatus;

    // 전문가 목록/상세용
    private String name;
    private String profileImagePath;
    private String specialty;
    private String description;
    private String longDescription;

    private Integer careerYear;
    private String education;
    private Boolean isOnline;

    private Double avgRating;
    private Integer reviewCount;

    private List<Map<String, Object>> careerList;
    private List<Map<String, Object>> educationList;
    private List<Map<String, Object>> certificateList;
    private List<InquiryDto> inquiryList;
}