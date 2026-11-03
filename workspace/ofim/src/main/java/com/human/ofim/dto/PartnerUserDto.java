package com.human.ofim.dto;

import org.springframework.web.multipart.MultipartFile;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PartnerUserDto extends MemberDto {
    
    private String businessNo;    
    private String ceoName;       
    private String openDate;      
    private String companyName;   
    private String mailOrderNo;   
    
    private String businessDocsPath;  
    private String mailOrderDocsPath; 
    
    private MultipartFile businessDocsFile;
    private MultipartFile mailOrderDocsFile;

    // [핵심 수정] DB의 0, 1, 2 상태를 온전히 담기 위해 Boolean -> Integer로 변경합니다.
    private Integer approveStatus;
    private String approveDate;
}