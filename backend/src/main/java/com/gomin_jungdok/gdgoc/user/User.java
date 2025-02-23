package com.gomin_jungdok.gdgoc.user;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int user_id;

    @Column(length = 225)
    private String id;

    @Column(length = 225)
    private String password;

    @Column(nullable = false, length = 225)
    private String nickname;

    @Column(length = 225)
    private String googleEmail;

    @Column(length = 225)
    private String socialId;

    @Column(nullable = false, length = 225)
    private String socialType;

    //@Column(name = "created_at", nullable = false, updatable = false)
    //private LocalDateTime createdAt = LocalDateTime.now();

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}

