package com.gomin_jungdok.gdgoc.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "카카오 로그인")
public class AuthController {

    @Value("${kakao.client}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    private final KakaoService kakaoService;

    @GetMapping("/kakao/login")
    @Operation(summary = "/api/auth/kakao/login")
    @ApiResponse(responseCode = "302",
            content = @Content(schema = @Schema(example = "{\"statusCode\": 302, \"message\": \"카카오 로그인\"}")))
    public ResponseEntity<Map<String, Object>> kakaoLogin() {

        String kakaoAuthUrl = "https://kauth.kakao.com/oauth/authorize"
                + "?client_id=" + clientId
                + "&redirect_uri=" + redirectUri
                + "&response_type=code";
        // + "&scope=email";  // 이메일 권한 요청

        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", kakaoAuthUrl)
                .build();
    }

    @GetMapping("/kakao/callback")
    @Operation(summary = "카카오 로그인 콜백", security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(example = "{\"statusCode\": 200, \"message\": \"로그인 성공\"}"))),
            @ApiResponse(responseCode = "401", content = @Content(schema = @Schema(example = "{\"statusCode\": 401, \"message\": \"토큰 생성 실패\"}")))
    })
    public ResponseEntity<Map<String, Object>> kakaoCallback(
            @Parameter(description = "카카오에서 전달된 인증 코드", required = true)
            @RequestParam("code") String code) {

        // ✅ 카카오 로그인 처리 및 Firebase Custom Token 생성
        Map<String, Object> firebaseToken = kakaoService.execKakaoLogin(code);
        // 검증
        String customToken = firebaseToken.get("customToken").toString();
        System.out.println("firebaseToken = " + firebaseToken);
        if (firebaseToken == null || !firebaseToken.containsKey("customToken")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("statusCode", 401, "message", "토큰 생성 실패"));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", 200);
        response.put("message", "로그인 성공");
        response.put("customToken", customToken);

        return ResponseEntity.ok(response);

    }

//    @GetMapping("/user")
//    public ResponseEntity<Map<String, Object>> getUser(@RequestHeader("Authorization") String accessToken) {
//        KakaoUserDto userInfo = kakaoService.getKakaoUserInfo(accessToken);
//    }




}