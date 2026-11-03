package com.human.ofim.dto;

import java.util.Date;

import lombok.Data;

@Data
public class BoardReportDto {

    private int postId;
    private int memberId;          // 신고자 회원번호

    private Date accusationDate;   // 신고일
    private Date acceptedDate;     // 접수일
    private Date sanctionDate;     // 제재일

    private String adminMemo;      // 관리자 메모
    private String content;        // 신고 내용
    private int code;              // 신고 사유 코드
    private int deleted;		   // 관리자 신고관리
    
    private Integer status;

    // 조인용
    private String reportCategoryName;
    private String reportCategoryDescription;
    private String reporterName;
    private String postTitle;
}