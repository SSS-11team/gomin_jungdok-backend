package com.gomin_jungdok.gdgoc.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "id")
    private String loginId;

    private String password;

    private String uid;

    @NotBlank(message = "닉네임은 필수입니다.")
    @Column(nullable = false)
    private String nickname;

    @Column(name = "google_email")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    private String email;

    @Column(name = "social_id")
    private String socialId;

    @Column(name = "social_type", nullable = false)
    private String socialType;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime created_at;

}
