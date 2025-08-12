package com.gomin_jungdok.gdgoc.notification;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NFMService {

    private final FirebaseMessaging firebaseMessaging;

    @Scheduled(cron = "0 0 00 * * ?")
    // @Scheduled(cron = "*/10 * * * * ?")
    public void pushAlarm() throws FirebaseMessagingException {
        try {
            Message message = createMessage();
            String response = firebaseMessaging.send(message);
            log.info("FCM 발송 성공: {}", response);
        } catch (FirebaseMessagingException e) {
            log.error("FCM 발송 실패", e);
        }
    }

    private Message createMessage() {
        return Message.builder()
                .setTopic("daily_question")
                .putData("title", "오늘의 고민 오픈!")
                .putData("body", "따끈따끈한 오늘의 고민이 공개됐어요! 지금 눌러서 어떤 고민일지 확인해 보세요.")
//                .setToken(notificationDto.getFcmToken())
                .build();
    }


}
