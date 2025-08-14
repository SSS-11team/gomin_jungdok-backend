package com.gomin_jungdok.gdgoc.user.Dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class ProfileImageRequestDto {
    private MultipartFile image;
}