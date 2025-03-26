package com.gomin_jungdok.gdgoc.auth;

import com.gomin_jungdok.gdgoc.auth.Dto.KakaoLoginResponse;
import com.gomin_jungdok.gdgoc.auth.Dto.UserInfoDto;
import com.gomin_jungdok.gdgoc.post.dto.PostDetailResponseDto;
import com.google.gson.JsonObject;
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

    private final KakaoService kakaoService;
//    private final UserRepository userRepository;

    @Value("${kakao.client}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

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
    @Operation(summary = "api/auth/kakao/callback")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = KakaoLoginResponse.class))),
            @ApiResponse(responseCode = "401", description = "토큰 생성 실패",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"statusCode\": 401, \"message\": \"토큰 생성 실패\"}")))
    })
    public ResponseEntity<Map<String, Object>> kakaoCallback(
            @Parameter(description = "카카오에서 전달된 인증 코드", required = true)
            @RequestParam("code") String code) {

        String accessToken = kakaoService.getKakaoAccessToken(code);
        UserInfoDto result = kakaoService.getKakaoUserInfo(accessToken);

        System.out.println("result = " + result);


        Map<String, Object> response = new HashMap<>();

        if (result.getId() == null)
        {
            response.put("statusCode", 401);
            response.put("message", "토큰 생성 실패");
        }
        else {

            response.put("statusCode", 200);
            response.put("message", "로그인 성공");
            response.put("result", result);
        }

        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + accessToken) // 헤더에 액세스 토큰 추가
                .body(response); // 바디에 유저 정보 포함

    }

    @GetMapping("/kakao/user")
    @Operation(summary = "api/auth/kakao/user",
            security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserInfoDto.class))),
            @ApiResponse(responseCode = "401", description = "토큰 생성 실패",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"statusCode\": 401, \"message\": \"토큰 생성 실패\"}")))
    })
    public UserInfoDto getUser(@RequestHeader("Authorization") String accessToken) {

// "Bearer " 제거
        if (accessToken.startsWith("Bearer ")) {
            accessToken = accessToken.substring(7); // "Bearer " 이후의 실제 토큰만 추출
        }


        UserInfoDto userInfo = kakaoService.getKakaoUserInfo(accessToken);
        System.out.println("userInfo = " + userInfo);
        return userInfo;
    }
}
