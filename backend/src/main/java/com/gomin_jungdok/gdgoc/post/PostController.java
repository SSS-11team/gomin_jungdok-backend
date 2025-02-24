package com.gomin_jungdok.gdgoc.post;

import com.gomin_jungdok.gdgoc.post.dto.PostDetailResponseDto;
import com.gomin_jungdok.gdgoc.post.dto.PostListResponseDto;
import com.gomin_jungdok.gdgoc.post.dto.PostWriteRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    //고민글 작성 api
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> createPost(@ModelAttribute PostWriteRequestDto requestDto) throws IOException {
        postService.createPost(requestDto);
        //TODO 응답 반환하는 전용 함수 따로 만들어서 리팩토링 적용하기, 에러 핸들링 추가하기
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", 201);
        response.put("message", "고민글 작성 성공");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //고민글 상세보기 api
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getPostDetail(@PathVariable Long id) {
        PostDetailResponseDto responseDto = postService.getPostDetail(id);

        //TODO 응답 반환하는 전용 함수 따로 만들어서 리팩토링 적용하기, 에러 핸들링 추가하기
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", 201);
        response.put("message", "고민글 상세보기 반환 성공");
        response.put("data", responseDto);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    //고민글 리스트 불러오기 api
    @GetMapping()
    public ResponseEntity<Map<String, Object>> getPostDetail(
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "last-id", required = false) Long lastId) {
        PostListResponseDto responseDto = postService.getPosts(size, lastId);

        //TODO 응답 반환하는 전용 함수 따로 만들어서 리팩토링 적용하기, 에러 핸들링 추가하기
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", 200);
        response.put("message", "고민글 리스트 반환 성공");
        response.put("data", responseDto);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
