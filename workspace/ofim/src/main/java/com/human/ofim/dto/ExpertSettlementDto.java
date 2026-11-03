package com.human.ofim.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true) // 부모 필드까지 비교!
@Data
public class ExpertSettlementDto extends ExpertUserDto {
	
	private String bankName;
	private String acctNo;
	private Long expertId;
	private String startDt;
	private String endDt;
	private BigDecimal totAmt;
	private BigDecimal currAmt;
	private BigDecimal paidAmt;
	private String payYn;
	private Integer consCnt;

	private String settlementMonth;   // 정산월 (yyyy-MM)
    private String expertName;        // 전문가 이름
    private String status;            // 대기 / 완료
    private LocalDate settlementDate; // 정산일
    
    private Integer consultId;              // 상담 ID
    private String consultType;             // 상담 유형
    private String memberName;              // 상담 회원명
    private LocalDateTime consultStartDate; // 상담 시작일시
    private LocalDateTime consultEndDate;   // 상담 종료일시
    private BigDecimal amount;              // 상담 1건 금액
    
    private String searchKeyword;    // 검색어 유지용

}
