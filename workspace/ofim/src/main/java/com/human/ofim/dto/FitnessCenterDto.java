package com.human.ofim.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class FitnessCenterDto {
	
	// 기본 피트니스 센터 정보
	private Integer centerNo;
	private Long memberId; 
	
	private String type; 
	private String centerName; 
	private String centerPhone;
	private String operTime; 
	private String facilityAddr;
	private String facilityAddrDetail;
	private String facilityAmenities;
	private String homepageUrl;
	private String description;
	private String writeDate;
	private String updateDate;
	private Double reviewScore;
	private Boolean dayPassYn; 
	
	private Long monthCost;
	private Long dayCost;
	
	private String snsLink1;
	private String snsLink2;
	private String addon;
	
	private Double lat; 
	private Double lon; 

	// UI 렌더링 가상 필드
	private String statusColor;
	private String imageUrl;
	private Integer reviewCount; 

	// --- 추가된 상세 운영시간 필드 ---
	private String weekdayOpen;
	private String weekdayClose;
	private String satOpen;
	private String satClose;
	private String sunOpen;
	private String sunClose;
	private String holidayOpen;
	private String holidayClose;
	private String breakStart;
	private String breakEnd;
	private String regularHoliday;
	private Boolean is24h; 
	private String ptAvailStart;
	private String ptAvailEnd;
	private String gxScheduleLink;
	private String holidayNotice;
	
	private Integer approveStatus;
	private String partnerName;
	private String loginId; 
	private Integer recommendCount;

	// [핵심] 광고 관련 필드
	private Integer isAd;
	private LocalDateTime adStartDate; // 광고 시작일
	private LocalDateTime adEndDate;   // 광고 종료일
	private Integer remainingDays;     // 남은 일수
	
	// Thymeleaf가 찾지 못해 에러를 뿜었던 광고 유형 필드 추가
	private String adType;

	// 메인 배너 광고용 데이터
	private String adTitle;
	private String adContent;

    // 🌟 [결제 시스템 1단계] 폼에서 여러 개의 회원권을 한번에 배열로 받기 위한 필드
    private List<String> ticketNames;
    private List<Integer> ticketPrices;
    private List<Integer> discountPrices;
    private List<Integer> periodMonths;

    // =========================================================
    // [긴급 에러 해결] 
    // 팀원이 Mapper.xml에는 #{sns_link1}이라고 언더바로 써놓고, 
    // 여기 DTO에는 snsLink1(카멜케이스)로 만들어놔서 터진 에러입니다.
    // 기존 Java 코드가 깨지지 않도록 MyBatis용 Getter/Setter만 추가합니다.
    // =========================================================
    public String getSns_link1() { return this.snsLink1; }
    public void setSns_link1(String sns_link1) { this.snsLink1 = sns_link1; }
    
    public String getSns_link2() { return this.snsLink2; }
    public void setSns_link2(String sns_link2) { this.snsLink2 = sns_link2; }
}