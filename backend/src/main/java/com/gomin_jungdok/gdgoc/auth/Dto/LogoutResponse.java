package com.gomin_jungdok.gdgoc.auth.Dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class LogoutResponse {

    @Schema(example = "200")
    private int statusCode;

    @Schema(example = "성공")
    private String message;

}
