package com.gomin_jungdok.gdgoc.report.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;


@Getter
@Setter
public class ReportRequestDto {
    @JsonProperty("targetType")
    private String targetType;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long targetId;

    public ReportRequestDto(
            String targetType,
            Long targetId) {
        this.targetType = targetType;
        this.targetId = targetId;
    }
}