package com.gomin_jungdok.gdgoc.user;

import com.gomin_jungdok.gdgoc.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ProfileImageService profileImageService;
    private JwtUtil jwtUtil;

    @Transactional
    public void deleteUser(Long userId) throws Exception {
        userRepository.deleteById(userId);
    }

//     public void updateNickname(Long userid, String newNickname) throws Exception {
     public void updateNickname(String jwtToken, String newNickname) throws Exception {
        System.out.println("newNickname = " + newNickname);

        Long userid = Long.parseLong(jwtUtil.validateAndGetUserId(jwtToken));

        User user = userRepository.findById(userid)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setNickname(newNickname);
        userRepository.save(user);

    }

//     public void updateProfile(String jwtToken, String newNickname) throws Exception {
    public void updateProfile(Long userid, MultipartFile newImage) throws Exception {
        System.out.println("newImage = " + newImage);

        // Long userid = Long.parseLong(jwtUtil.validateAndGetUserId(jwtToken));

        profileImageService.uploadProfileImage(newImage, userid);




    }


}
