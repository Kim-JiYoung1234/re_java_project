package com.human.ofim.dto;

import lombok.Data;

@Data
public class CenterTicketDto {
    private Integer ticketNo;
    private Integer centerNo;
    private String ticketName;
    private Integer ticketPrice;
    private Integer discountPrice;
    private Integer periodMonths;
}