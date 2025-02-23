package com.gomin_jungdok.gdgoc.vote.DTO;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VoteRequestDTO {
    @Min(1) @Max(2) // 1 또는 2만 허용
    // private Long user_id;
    // private int post_id;
    private int vote;
}
