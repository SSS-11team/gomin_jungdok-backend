package com.gomin_jungdok.gdgoc.notification;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
@Service
@RequiredArgsConstructor
public class NFMService {

    private final FirebaseMessaging firebaseMessaging;

    @Scheduled(cron = "0 0 00 * * ?")
    public void pushAlarm() throws FirebaseMessagingException {
        Message message = createMessage();
        firebaseMessaging.send(message);
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
