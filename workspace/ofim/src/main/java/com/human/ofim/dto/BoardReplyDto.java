package com.human.ofim.dto;

import java.util.Date;
import lombok.Data;

@Data
public class BoardReplyDto {

	private int replyId; // 댓글 고유 번호 (PK)
	private int postId; // 부모 게시글 번호 (FK)
	private int writerId; // 작성자 번호 (FK)
	private String content; // 댓글 내용
	private Date createdDate; // 작성일
	private Integer gradeId; // 유료등급
	// [대댓글 핵심 추가]
	private Integer parentId; // 부모 댓글 번호

	private String title;
	private String categoryName;
	private String subCategory;
	private String writerName;
	private String memberType;
	private String memberTypeName;
	private String writerProfile; // 보드에서 프로필사진 보여주기용
	
	
	// 🌟 [추가] 활동 점수 (DB의 activity_point와 매핑)
	private int activityPoint;

	// 🌟 [좋아요 기능 추가]
	private int likeCount; // 해당 댓글의 총 좋아요 수 (SQL의 likeCount와 매핑)
	private int isLiked; // 내가 좋아요를 눌렀는지 여부 (1: 눌렀음, 0: 안눌렀음)

	/**
	 * 🌟 ofim 전용 등급 이름 반환 로직 (댓글용)
	 */
	public String getDiaterRank() {
		if (activityPoint >= 5000)
			return "언더아머";
		if (activityPoint >= 2500)
			return "머슬 빌더";
		if (activityPoint >= 1000)
			return "버닝 파이터";
		if (activityPoint >= 300)
			return "오핌 러너";
		return "비기너 루키";
	}
}