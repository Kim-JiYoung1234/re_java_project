package com.human.ofim.dto;

import lombok.Data;

@Data
public class ReviewDto {
    private Integer reviewId;
    private Integer centerNo;
    private Long memberId;
    private String createdDate;
    private String content;
    private Integer rating;       // [추가] 개별 별점 (1~5)
    
 // [새로 추가될 필드]
    private String replyContent;  // 파트너사 답글 내용
    private String replyDate;     // 파트너사 답글 작성일
    
    // DB 조인으로 가져올 가상 필드
    private String nickname;      // 작성자 닉네임
    private Integer recommendCount; // 리뷰 추천 수
    private String centerName;     // 센터 이름
}