package com.gomin_jungdok.gdgoc.auth.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KakaoUserDto {

    private String id;   // uid
    private String nickname;
    private String email;
//    private String profileImage;


}
