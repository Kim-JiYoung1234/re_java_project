package com.human.ofim.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class BoardLikeDto {
    private int likeId;
    private int postId;
    private int memberId;
    private LocalDateTime createdDate;
}