package com.gomin_jungdok.gdgoc.notification;

import com.gomin_jungdok.gdgoc.user.User;
import com.gomin_jungdok.gdgoc.user.UserRepository;
import com.google.firebase.messaging.FirebaseMessagingException;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NFMService NFMService;
//
//    @PostMapping("/api/post/push")
//    public ResponseEntity<String> pushNotification() throws FirebaseMessagingException {
//        NFMService.pushAlarm();
//        return ResponseEntity.ok("푸시 알림 전송용");
//    }



}
