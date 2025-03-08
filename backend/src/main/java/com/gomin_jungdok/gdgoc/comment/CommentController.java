package com.gomin_jungdok.gdgoc.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/{post_id}")
    public ResponseEntity<Comment> createComment(@RequestBody CommentCreateDTO commentCreateDTO, @PathVariable long post_id) {
        Comment savedComment = commentService.saveComment(commentCreateDTO, post_id);
        return ResponseEntity.ok(savedComment);
    }
}
