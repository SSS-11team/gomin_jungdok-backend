package com.gomin_jungdok.gdgoc.post.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PostListDetailResponseDto {
    private Long id;

    @JsonProperty("isVoted")
    private boolean isVoted;

    @JsonProperty("isMine")
    private boolean isMine;

    @JsonProperty("isAi")
    private boolean isAi;

    private String title;
    private String option1Content;
    private String option2Content;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long option1Vote;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long option2Vote;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String option1Percentage;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String option2Percentage;

    public PostListDetailResponseDto(
            Long id,
            boolean isVoted,
            boolean isMine,
            boolean isAi,
            String title,
            String option1Content,
            String option2Content,
            Long option1Vote,
            Long option2Vote,
            String option1Percentage,
            String option2Percentage) {
        this.id = id;
        this.isVoted = isVoted;
        this.isMine = isMine;
        this.isAi = isAi;
        this.title = title;
        this.option1Content = option1Content;
        this.option2Content = option2Content;
        this.option1Vote = option1Vote;
        this.option2Vote = option2Vote;
        this.option1Percentage = option1Percentage;
        this.option2Percentage = option2Percentage;
    }
}