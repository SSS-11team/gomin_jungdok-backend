package com.gomin_jungdok.gdgoc.vote;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class VoteController {
    private final voteService voteService;
    // 고민 투표
    @PostMapping("/post/{id}/vote")
    public ResponseEntity<Vote> createVote(@PathVariable int id, @RequestBody VoteDTO voteDTO) {
        Vote saveVote = voteService.saveVote(id, voteDTO);
        return ResponseEntity.ok(saveVote);
    }
}
