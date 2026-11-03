package com.human.ofim.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagerDto {
	
	private int pageNo;
	private int pageSize;
	private int pagerSize;
	private int dataCnt;
	private int pageCnt;
	private int pageStart;
	private int pageEnd;
	private String linkUrl;

}
