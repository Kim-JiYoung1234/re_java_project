package com.human.ofim.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AdminConsultDto extends ConsultDto {

    private String adminReplyContent;   // 관리자 답변 입력값용
    private Integer adminId;            // 처리 관리자
    private String actionType;          // complete, changeExpert, release ...
    private Long newExpertId;           // 변경할 전문가
    private String reason;              // 처리 사유
}