package com.gomin_jungdok.gdgoc.jwt;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthTokens {

    @Schema(example = "jwtAccessToken")
    private String accessToken;
    @Schema(example = "jwtRefreshToken")
    private String refreshToken;
//    private String grantType;
//    private Long expiresIn;
//
//    public static AuthTokens of(String accessToken, String refreshToken, String grantType, Long expiresIn) {
//        return new AuthTokens(accessToken, refreshToken, grantType, expiresIn);
//    }

    public static AuthTokens of(String accessToken, String refreshToken) {
        return new AuthTokens(accessToken, refreshToken);
    }
}
