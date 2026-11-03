package com.human.ofim.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VisitorSummaryDto {
	private long todayVisitors;
	private long weekAvgVisitors;
}