package com.gomin_jungdok.gdgoc.post;

import com.gomin_jungdok.gdgoc.post.dto.PostWriteRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    //고민글 작성 api
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> createPost(@ModelAttribute PostWriteRequestDto requestDto) throws IOException {
        postService.createPost(requestDto);
        //TODO 응답 반환하는 전용 함수 따로 만들어서 리팩토링 적용하기, 에러 핸들링 추가하기
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "고민글 작성 완료"));
    }
}
