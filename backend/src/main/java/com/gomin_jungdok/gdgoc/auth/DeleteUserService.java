package com.gomin_jungdok.gdgoc.auth;

import com.gomin_jungdok.gdgoc.user.UserRepository;
import com.gomin_jungdok.gdgoc.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteUserService {

    private final UserService userService;
    private final UserRepository userRepository;


    @Transactional
    public void deleteUser(Long userId) throws Exception {

        userRepository.deleteById(userId);


    }

}
