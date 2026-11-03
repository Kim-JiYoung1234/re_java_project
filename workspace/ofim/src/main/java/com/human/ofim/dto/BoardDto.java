package com.human.ofim.dto;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class BoardDto {

	private int postId;
	private int categoryId;
	private int writerId;

	private String title;
	private String content;

	private Date createdDate;
	private int viewCount;
	private int deleted;

	private String subCategory;
	private String imageName;

	private String videoUrl;

	// 🌟 [수정] int -> Integer로 변경 (DB값이 null일 때의 에러 방지)
	private Integer activityPoint;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date endDate;

	// 집계/조인용
	private int likeCount;
	private int replyCount;
	private int adminReplyCount;

	private String categoryName;
	private String writerName;
	private String writerProfile; //(보드에서 프로필 사진)

	private String memberType;
	private String memberTypeName;
	private Integer gradeId;

	// 업로드용 (DB 컬럼 아님)
	private MultipartFile uploadFile;

	// 첨부파일 테이블이 실제 있을 때만 사용
	private List<BoardAttachDto> attachments;

	/**
	 * 🌟 [추가] 챌린지 D-Day 숫자 계산 로직 오늘 날짜와 endDate 사이의 일수 차이를 반환합니다.
	 */
	public long getDDayCount() {
		if (this.endDate == null) {
			return -999; // 날짜가 설정되지 않은 경우
		}

		// 시간차 계산을 위해 Calendar 사용 (java.util.Date 호환)
		Calendar today = Calendar.getInstance();
		today.set(Calendar.HOUR_OF_DAY, 0);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);
		today.set(Calendar.MILLISECOND, 0);

		Calendar end = Calendar.getInstance();
		end.setTime(this.endDate);
		end.set(Calendar.HOUR_OF_DAY, 0);
		end.set(Calendar.MINUTE, 0);
		end.set(Calendar.SECOND, 0);
		end.set(Calendar.MILLISECOND, 0);

		long diffMillis = end.getTimeInMillis() - today.getTimeInMillis();
		return diffMillis / (24 * 60 * 60 * 1000); // 밀리초를 일(day)로 변환
	}

	/**
	 * 🌟 [추가] 챌린지 상태 텍스트 반환 로직 형의 기획대로 "진행 중"을 유지하며 D-Day를 덧붙입니다.
	 */
	public String getChallengeStatusText() {
		long dDay = getDDayCount();

		if (dDay < 0) {
			return "종료";
		} else if (dDay == 0) {
			return "진행 중 (D-Day)";
		} else {
			return "진행 중 (D-" + dDay + ")";
		}
	}

	/**
	 * 🌟 ofim 전용 등급 이름 반환 로직
	 */
	public String getDiaterRank() {
		int pts = (this.activityPoint != null) ? this.activityPoint : 0;

		if (pts >= 5000)
			return "언더아머";
		if (pts >= 2500)
			return "머슬 빌더";
		if (pts >= 1000)
			return "버닝 파이터";
		if (pts >= 300)
			return "오핌 러너";
		return "비기너 루키";
	}

	public String getYoutubeId() {
		if (videoUrl == null || videoUrl.trim().isEmpty()) {
			return null;
		}

		String id = null;
		try {
			String trimmedUrl = videoUrl.trim();

			if (trimmedUrl.contains("v=")) {
				String[] parts = trimmedUrl.split("v=");
				if (parts.length > 1) {
					id = parts[1].split("&")[0];
				}
			} else if (trimmedUrl.contains("youtu.be/")) {
				String[] parts = trimmedUrl.split("youtu.be/");
				if (parts.length > 1) {
					id = parts[1].split("\\?")[0];
				}
			}
		} catch (Exception e) {
			return null;
		}
		return id;
	}
}