package com.gomin_jungdok.gdgoc.auth;

import com.gomin_jungdok.gdgoc.auth.Dto.UserInfoDto;
import com.gomin_jungdok.gdgoc.jwt.AuthTokens;
import com.gomin_jungdok.gdgoc.jwt.AuthTokensGenerator;
import com.gomin_jungdok.gdgoc.jwt.JwtUtil;
import com.gomin_jungdok.gdgoc.user.UserRepository;
import com.gomin_jungdok.gdgoc.user.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.gomin_jungdok.gdgoc.auth.Converter.AuthConverter.toUserFirebase;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppleAndGoogleService {

    private final UserRepository userRepository;
    private final AuthTokensGenerator authTokensGenerator;
    private final JwtUtil jwtTokenProvider;

    public AuthTokens validateFirebaseToken(String idToken) throws Exception {
        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
        String uid = decodedToken.getUid();

        System.out.println("uid = " + uid);

        User user = userRepository.findByUid(uid);   //수정 필요 string int

        if (user == null) {
            // 회원가입 로직
            System.out.println("user is null : apple / google");
            user = toUserFirebase(decodedToken);
            userRepository.save(user);
        }

        Long userId = user.getId();

        AuthTokens authTokens = authTokensGenerator.generate(userId.toString());
        return authTokens;
    }

    public UserInfoDto getUserInfoByJwt(String accessToken) {
        accessToken = accessToken.substring(7);
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
