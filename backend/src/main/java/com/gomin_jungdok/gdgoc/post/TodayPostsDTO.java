package com.gomin_jungdok.gdgoc.post;

import com.gomin_jungdok.gdgoc.vote.VoteResultDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor  //기본 생성자 (필수)
@AllArgsConstructor //모든 필드를 받는 생성자 자동 생성
public class TodayPostsDTO  {
    private long postId;       //게시글 ID

    private String title;     //게시글 제목

    private String description; //게시글 내용

    private List<VoteResultDTO> voteResults; //투표 결과 (퍼센트)

    private long voteCount;

    private long commentCount; //댓글 수
}
