package com.gomin_jungdok.gdgoc.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class AuthTokensGenerator {

    private static final String BEARER_TOKEN = "Bearer ";
    private static final long ACCESS_TOKEN_EXPIRATION_TIME = 24 * 60 * 60; // 1시간
    private static final long REFRESH_TOKEN_EXPIRATION_TIME = 24 * 60 * 60 * 1000; // 1000 시간

    private final JwtTokenProvider jwtTokenProvider;

    //id 받아서 AccessToken 생성
    public AuthTokens generate(String id) {
        long now = (new Date()).getTime();
        Date accessTokenExpiresAt = new Date(now + ACCESS_TOKEN_EXPIRATION_TIME);
        Date refreshTokenExpiresAt = new Date(now + REFRESH_TOKEN_EXPIRATION_TIME);

        //String subject = email.toString()'
        String accessToken = jwtTokenProvider.accessTokenGenerate(id, accessTokenExpiresAt);
        String refreshToken = jwtTokenProvider.refreshTokenGenerate(refreshTokenExpiresAt);

        return AuthTokens.of(accessToken, refreshToken, BEARER_TOKEN, ACCESS_TOKEN_EXPIRATION_TIME);
    }
}
