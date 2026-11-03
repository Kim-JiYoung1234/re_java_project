package com.human.ofim.dto;

import lombok.Data;

@Data
public class ConsultPendingDto {
    private Integer consultId;
    private String userName;
    private String consultType;
    private String consultContent;

}