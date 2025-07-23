package com.gomin_jungdok.gdgoc.auth;

import com.gomin_jungdok.gdgoc.auth.Dto.UserInfoDto;
import com.gomin_jungdok.gdgoc.jwt.AuthTokens;
import com.gomin_jungdok.gdgoc.jwt.AuthTokensGenerator;
import com.gomin_jungdok.gdgoc.jwt.JwtUtil;
import com.gomin_jungdok.gdgoc.user.UserRepository;
import com.gomin_jungdok.gdgoc.user.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.gomin_jungdok.gdgoc.auth.Converter.AuthConverter.toUserFirebase;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppleService {

    private final UserRepository userRepository;
    private final AuthTokensGenerator authTokensGenerator;
    private final JwtUtil jwtTokenProvider;


    // UserInfoDto
    // Long id, String email, String socialType, String nickname, Date createAt

    public String createFirebaseCustomToken(UserInfoDto userInfo) throws Exception {

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

        User user = userRepository.findByEmail(email);
        if (user != null) {
            user.setUid(firebaseUid); // Firebase의 uid 저장
            userRepository.save(user);
        }

        // 5. Firebase Custom Token 생성 후 리턴
        return FirebaseAuth.getInstance().createCustomToken(firebaseUid);

    }

    public AuthTokens validateFirebaseToken(String idToken) throws Exception {
        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
        String uid = decodedToken.getUid();

        System.out.println("uid = " + uid);

        User user = userRepository.findByUid(uid);   //수정 필요 string int

        if (user == null) {
            // 회원가입 로직
            System.out.println("user is null");
            user = toUserFirebase(decodedToken);
            userRepository.save(user);
        }

        Long userId = user.getId();

        AuthTokens authTokens = authTokensGenerator.generate(userId.toString());
        return authTokens;
    }

    public UserInfoDto getUserInfoByJwt(String accessToken) {
        Long userId = Long.parseLong(jwtTokenProvider.validateAndGetUserId(accessToken));
        UserInfoDto userInfo = new UserInfoDto();
        User user = userRepository.findById(userId).get();


        userInfo.setId(userId);
        userInfo.setCreatedAt(user.getCreatedAt());
        userInfo.setNickname(user.getNickname());
        userInfo.setEmail(user.getEmail());
        userInfo.setSocialType(user.getSocialType());

        return userInfo;
    }
}
