package com.gomin_jungdok.gdgoc.post;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@NoArgsConstructor  // 기본 생성자 (필수)
@AllArgsConstructor // 모든 필드를 받는 생성자 자동 생성
public class TodayPostsDTO  {
    private long postId;       // 게시글 ID

    private String title;     // 게시글 제목

    private String description; // 게시글 내용

    private String category;

    private long voteCount;
}
