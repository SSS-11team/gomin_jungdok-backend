package com.gomin_jungdok.gdgoc.auth.Dto;

import lombok.Data;

@Data
public class AppleTokenResponse {
    String accessToken;
    Integer expiresIn;
    String idToken;
    String refreshToken;
    String tokenType;
}
