package com.gomin_jungdok.gdgoc.vote.DTO;

import lombok.*;

@Getter
@Setter
public class VoteResponseDTO {
    // private Long postId;  // 게시글의 id
    private int VoteOfOption1;
    private int VoteOfOption2;

    private double option1Percentage;
    private double option2Percentage;
}
