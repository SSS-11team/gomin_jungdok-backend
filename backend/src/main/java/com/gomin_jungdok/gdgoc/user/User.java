package com.gomin_jungdok.gdgoc.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Long Id;

    //user 자체 회원가입 시 id
    @Column(name = "id", length = 225)
    private String userId;

    @Column(length = 225)
    private String password;

    @Column(nullable = false, length = 225)
    private String nickname;

    @Column(name = "google_email", length = 225)
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    private String googleEmail;

    @Column(name = "social_id", length = 225)
    private String socialId;

    @Column(name = "social_type", nullable = false, length = 225)
    private String socialType;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();

    //firebase 사용 시 필요한 변수값
    @Column(name = "uid", nullable = false)
    private String uid;

    @Column(name = "profile_image")
    private String profileImage;
}