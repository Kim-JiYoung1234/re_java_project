package com.human.ofim.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleContentDto {
    private Long id;
    private String title;
    private int viewCount;          // 인기콘텐츠에서 사용
    private LocalDateTime createdAt; // 최신글에서 사용
}