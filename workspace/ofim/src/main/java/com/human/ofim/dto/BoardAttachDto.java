package com.human.ofim.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardAttachDto {

    private int attachId;
    private int postId;
    private String userFileName;
    private String savedFileName;
    private int downloadCount;
}