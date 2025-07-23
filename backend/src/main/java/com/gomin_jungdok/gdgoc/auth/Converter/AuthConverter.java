package com.gomin_jungdok.gdgoc.auth.Converter;

import com.gomin_jungdok.gdgoc.user.User;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class AuthConverter {

    public static User toUserKakao(Long id) {

        return User.builder()
                .nickname("익명")
                .socialId(id)
                .socialType("KAKAO")
                .createdAt(new Date())
                .build();
    }

    public static User toUserFirebase(FirebaseToken decodedToken) {

        String uid = decodedToken.getUid();
        String email = decodedToken.getEmail();
        String socialType = determineSocialType(decodedToken);
        String picture = decodedToken.getPicture();

        return User.builder()
                .uid(uid)
                .nickname("익명")
                .createdAt(new Date())
                .profileImage(picture)
                .email(email)
                .socialType(socialType)
                .build();
    }

    private static String determineSocialType(FirebaseToken decodedToken) {
        Object provider = decodedToken.getClaims().get("firebase").getClass().toString();
        System.out.println("provider = " + provider.toString());
        if (provider == null) return "UNKNOWN";

        switch (provider.toString()) {
            case "class com.apple.api.client.util.ArrayMap":
                System.out.println("v" + provider);
                return "APPLE";
            case "class com.google.api.client.util.ArrayMap":
                System.out.println("provider = " + provider);
                return "GOOGLE";
            default:
                return "UNKNOWN";
        }
    }


}
