package com.human.ofim.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminExpertConsultStatsDto {

    private long totalConsultCount;
    private long totalClientCount;
    private long totalMinutes;

}