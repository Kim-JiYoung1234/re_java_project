package com.human.ofim.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminBoardItemDto {

	private int postId;
    private String title;
    private int viewCount;
    private int likeCount;
    private String categoryName;
    private String createdDate;
}