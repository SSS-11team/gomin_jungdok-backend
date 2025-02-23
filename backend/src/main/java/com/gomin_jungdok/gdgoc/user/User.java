package com.gomin_jungdok.gdgoc.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    //user 자체 회원가입 시 id
    @Column(name = "id")
    private String userId;

    @Column(name = "password")
    private String password;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "google_email")
    private String googleEmail;

    @Column(name = "social_id")
    private String socialId;

    @Column(name = "social_type", nullable = false)
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
