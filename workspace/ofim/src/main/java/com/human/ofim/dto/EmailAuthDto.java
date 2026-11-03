package com.human.ofim.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class EmailAuthDto {
    private int authId;
    private String email;
    private String token;
    private String type;          // "PW_RESET" 또는 "SIGNUP"
    private LocalDateTime expiredAt; // 만료 시간
    private boolean isUsed;       // 사용 여부 (0: 미사용, 1: 사용됨)
    private LocalDateTime createdAt;
}