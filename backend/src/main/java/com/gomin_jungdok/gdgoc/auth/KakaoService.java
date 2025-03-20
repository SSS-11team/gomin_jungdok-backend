package com.gomin_jungdok.gdgoc.auth;

import com.gomin_jungdok.gdgoc.auth.Dto.KakaoTokenResponseDto;
import com.gomin_jungdok.gdgoc.auth.Dto.KakaoUserDto;
import com.gomin_jungdok.gdgoc.user.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static com.gomin_jungdok.gdgoc.auth.Converter.KakaoAuthConverter.toUser;

@Service
public class KakaoService {

    private static final Logger log = LoggerFactory.getLogger(KakaoService.class);

    @Value("${kakao.client}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    private final KakaoRepository kakaoRepository; // UserRepository 주입

    @Autowired
    public KakaoService(KakaoRepository kakaoRepository) {
        this.kakaoRepository = kakaoRepository;
    }

    // 카카오 로그인 프로세스 진행 (최종 목표는 Firebase CustomToken 발행)
    @Transactional
    public Map<String, Object> execKakaoLogin(String code) {
        Map<String, Object> result = new HashMap<String,Object>();

        // 1. 엑세스 토큰 받기
        String accessToken = getKakaoAccessToken(code);
        result.put("access_token", accessToken);
        System.out.println("accessToken = " + accessToken);

        // 2. 사용자 정보 읽어오기
        KakaoUserDto userInfo = getKakaoUserInfo(accessToken);

        // String id = userInfo.getId();  // 3963637345
        String email = userInfo.getEmail();
        result.put("kakao", userInfo);
        System.out.println("user_info = " + userInfo);

        // 3. firebase CustomToken 발행
        User user = kakaoRepository.findByEmail(email);
        System.out.println("찾은 사용자 : " + (user != null ? user.getId() : "없음"));

        if (user == null) {
            // 새로운 사용자 등록
            user = toUser(userInfo);
            kakaoRepository.save(user); // 사용자 정보 저장
            System.out.println("사용자 저장 완료 : " + user.getId());
            System.out.println("user.getNickname() = " + user.getNickname());
        }

        String customToken = "";
        try {
            // 4. Firebase Custom Token 발급 & Firebase UID 저장
            customToken = createFirebaseCustomToken(userInfo);

            // Firebase UID 가져오기
            String firebaseUid = FirebaseAuth.getInstance().getUserByEmail(email).getUid();

            // 5. 사용자 정보에 Firebase UID 저장
            user.setUid(firebaseUid);
            kakaoRepository.save(user);

            result.put("customToken", customToken);
            result.put("firebaseUid", firebaseUid); // Firebase UID 추가
            result.put("errYn", "N");
            result.put("errMsg", "");
        } catch (FirebaseAuthException e) {
            System.out.println("FirebaseAuthException.class.getName() = " + FirebaseAuthException.class.getName());
            result.put("errYn", "N");
            result.put("errMsg", "FirebaseException : " + e.getMessage());
        } catch (Exception e) {
            System.out.println("e = " + e);
            result.put("errYn", "N");
            result.put("errMsg", "Exception : " + e.getMessage());
        }

        System.out.println("customToken = " + customToken);

        // 4. 클라이언트에서 Firebase 로그인 후 ID 토큰을 가져오도록 유도
        result.put("customToken", customToken);
        result.put("errYn", "N");
        result.put("errMsg", "");

        System.out.println("result = " + result);
        return result;  // customToken만 포함된 Map 반환
    }

    public String getKakaoAccessToken(String code) {
        String reqURL = "https://kauth.kakao.com/oauth/token";

        RestTemplate rt = new RestTemplate();

        // HttpHeader 오브젝트 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded");

        // HttpBody 데이터를 받는 오브젝트 생성
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);

        // HttpHeader와 HttpBody를 하나의 오브젝트에 담기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

        // Http 요청하기 - Post 방식으로 - 그리고 response 변수의 응답 받음.
        ResponseEntity<KakaoTokenResponseDto> response = rt.exchange(
                reqURL, HttpMethod.POST, kakaoTokenRequest, KakaoTokenResponseDto.class);


        return response.getBody() != null ? response.getBody().getAccessToken() : null;
    }

    public KakaoUserDto getKakaoUserInfo(String accessToken) {

        // 요청하는 클라이언트마다 가진 정보가 다를 수 있기에 HashMap 타입으로 선언
        KakaoUserDto userInfo = new KakaoUserDto();
        String reqURL = "https://kapi.kakao.com/v2/user/me";

        try{
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // System.out.println(conn.getResponseCode());
            conn.setRequestMethod("GET");

            // 요청에 필요한 Header에 필요한 내용
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);

            int responseCode = conn.getResponseCode();
            log.debug("responseCode : " + responseCode);

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            log.debug("response body : " + result);

            JsonElement element = JsonParser.parseString(result);
            System.out.println("element = " + element);

            String email = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("email").getAsString();
            // String id = element.getAsJsonObject().get("id").getAsString();  // "id" 값을 String으로 가져오기

            System.out.println("getKakaoUserInfo");
            // System.out.println("id = " + id);  // kakao안에 있는 userid
            // userInfo.setId(id);
            userInfo.setEmail(email);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return userInfo;
    }

    public String kakaoLogout(String accessToken) {

        String reqURL = "https://kapi.kakao.com/v2/logout";
        try {
            URL url = new URL(reqURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // POST 요청을 위해 기본값이 false인 setDoOutput을 true로
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            // 요청에 필요한 Header에 포함된 내용
            int responseCode = conn.getResponseCode();
            log.debug("responseCode : " + responseCode);

            // 요청을 통해 얻은 JSON 타입의 Response 메시지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            log.debug("response body : " + result);
            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return accessToken;
    }

    public String createFirebaseCustomToken(KakaoUserDto userInfo) throws Exception {

        UserRecord userRecord;
        String email = userInfo.getEmail();

        // 이메일이 없으면 예외 처리
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("이메일이 필요합니다.");
        }

        // 1. 사용자 정보로 파이어 베이스 유저정보 update, 사용자 정보가 있다면 userRecord에 유저 정보가 담긴다.
        try {
            // 이메일을 기반으로 Firebase 사용자 정보 가져오기
            userRecord = FirebaseAuth.getInstance().getUserByEmail(email);
            // 1-2. 사용자 정보가 없다면 > catch 구분에서 createUser로 사용자를 생성한다.
        } catch (FirebaseAuthException e) {
            // 사용자가 존재하지 않으면 새로 생성 (uid는 자동 생성됨)
            UserRecord.CreateRequest createRequest = new UserRecord.CreateRequest()
                    .setEmail(email)
                    .setEmailVerified(true) // 이메일 인증 여부 (필요하면 true)
                    .setDisplayName(userInfo.getNickname());

            userRecord = FirebaseAuth.getInstance().createUser(createRequest);

        }
        // 3. Firebase에서 생성된 uid를 가져옴
        String firebaseUid = userRecord.getUid();

        // 4. 해당 사용자의 DB 정보 업데이트 (uid 저장)
//        User user = kakaoRepository.findByEmail(email);
//        if (user != null) {
//            user.setUid(firebaseUid); // Firebase의 uid 저장
//            kakaoRepository.save(user);
//        }

        // 5. Firebase Custom Token 생성 후 리턴
        return FirebaseAuth.getInstance().createCustomToken(firebaseUid);

    }
}
