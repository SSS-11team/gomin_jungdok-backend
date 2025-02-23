package com.gomin_jungdok.gdgoc.vote;


import com.gomin_jungdok.gdgoc.vote.DTO.VoteRequestDTO;
import com.gomin_jungdok.gdgoc.vote.DTO.VoteResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST})
public class VoteController {
    private final VoteService voteService;

//    @PostMapping("/1L/vote")
//    public ResponseEntity<Map<String, Object>> vote(@RequestBody VoteRequestDTO voteRequest) {
    @PostMapping("/{id}/vote")
    @Operation(summary = "swagger_test_operation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공",
                    content = {@Content(schema = @Schema(implementation = ResponseEntity.class))}),
            @ApiResponse(responseCode = "404", description = "해당 ID의 유저가 존재하지 않습니다."),
            @ApiResponse(responseCode = "500", description = "서버의 어딘가가 잘못되었습니다.")
    })
    public ResponseEntity<Map<String, Object>> vote(@PathVariable Long id, @RequestBody VoteRequestDTO voteRequest) {

        // postid로 post 불러와서 투표 결과 가져옴
        // 투표 결과 수정(1 or 2에 +1)
        // 투표 결과 저장 후 리턴
        VoteResponseDTO result = voteService.vote(1L, id, voteRequest);

        // 응답 데이터 생성
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", HttpStatus.OK.value());
        response.put("message", "고민글 투표 성공");
        response.put("data", result);


        return ResponseEntity.ok(response);
    }



}
