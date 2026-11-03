package com.human.ofim.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class CertificateDto {

    private Long certificationId;
    private Long memberId;

    private String certName;
    private String issuer;
    private String certNo;

    private LocalDate issuedDate;
    private LocalDate expiredDate;

    private String rejectReason;
    private Integer verifyStatus; // 0=대기,1=승인,2=반려
    private LocalDateTime verifiedDate;

    private LocalDateTime createdDate;

    private String certFilePath;

    private MultipartFile certFile;
}