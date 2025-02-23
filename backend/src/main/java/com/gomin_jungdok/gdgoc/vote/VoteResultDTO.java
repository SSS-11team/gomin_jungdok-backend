package com.gomin_jungdok.gdgoc.vote;
import lombok.*;

@Getter
@NoArgsConstructor  // 기본 생성자 (필수)
@AllArgsConstructor // 모든 필드를 받는 생성자 자동 생성
public class VoteResultDTO {
    private String option; // 투표 항목
    private double percentage; // 투표 비율 (%)
}
