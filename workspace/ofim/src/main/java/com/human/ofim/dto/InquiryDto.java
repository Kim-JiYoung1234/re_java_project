package com.human.ofim.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class InquiryDto {

    private Long inquiryId;
    private Long consultId;
    private String content;
    private LocalDateTime writeDate;
    private String commentType; // 질문, 답변
    private String fileOriginalName;
    private String fileStoredName;
    private String filePath;
}