package com.gomin_jungdok.gdgoc.auth.Converter;

import com.gomin_jungdok.gdgoc.auth.Dto.UserInfoDto;
import com.gomin_jungdok.gdgoc.user.User;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class KakaoAuthConverter {

    public static User toUserKakao(String email, Long id) {

        return User.builder()
                .nickname("익명")
                .socialId(id)
                .socialType("KAKAO")
                .createdAt(new Date())
                .build();
    }
}
