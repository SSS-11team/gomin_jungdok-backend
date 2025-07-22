package com.gomin_jungdok.gdgoc.report;

import com.gomin_jungdok.gdgoc.report.dto.ReportRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
@Tag(name = "/api/report")
public class ReportController {
    private final ReportService reportService;

    //신고 api
    @PostMapping
    @Operation(summary = "게시글 및 댓글 신고")
    @ApiResponse(responseCode = "201", description = "신고 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(example = "{\"statusCode\":201,\"message\":\"신고 성공\"}")))
    public ResponseEntity<Map<String, Object>> createReport(@RequestBody ReportRequestDto requestDto) {
        log.debug("Received DTO: {}", requestDto);

        reportService.createReport(requestDto);

        //TODO 응답 반환하는 전용 함수 따로 만들어서 리팩토링 적용하기, 에러 핸들링 추가하기
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", 201);
        response.put("message", "신고 성공");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
