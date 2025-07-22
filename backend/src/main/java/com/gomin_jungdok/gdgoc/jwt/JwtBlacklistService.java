package com.gomin_jungdok.gdgoc.jwt;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.gomin_jungdok.gdgoc.jwt.AuthTokensGenerator.ACCESS_TOKEN_EXPIRATION_TIME;

@Service
public class JwtBlacklistService {

    private final Map<String, Long> blacklist = new ConcurrentHashMap<>();

    public void blacklist(String token) {
        long expiry = extractExpiry(token);
        blacklist.put(token, expiry);
    }

    public boolean isBlacklisted(String token) {
        Long expiry = blacklist.get(token);
        if (expiry == null) {
            return false;
        }
        if(expiry < System.currentTimeMillis()) {
            blacklist.remove(token);
            return false;
        }
        return true;
    }

    private long extractExpiry(String token) {
        try {
            String[] parts = token.split("\\.");
            String payload = new String(java.util.Base64.getDecoder().decode(parts[1]));
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            Map<String, Object> claims = mapper.readValue(payload, Map.class);
            int exp = (int) claims.get("exp");
            return exp * 1000L;


        } catch (Exception e) {
            return System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME;
        }
    }


}
