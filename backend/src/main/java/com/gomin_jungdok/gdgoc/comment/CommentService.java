package com.gomin_jungdok.gdgoc.comment;

import com.gomin_jungdok.gdgoc.post.Post;
import com.gomin_jungdok.gdgoc.post.PostRepository;
import com.gomin_jungdok.gdgoc.user.User;
import com.gomin_jungdok.gdgoc.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public Comment saveComment(CommentCreateDTO commentCreateDTO, long id) {
        User user = userRepository.findById(commentCreateDTO.getUser_id())
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + commentCreateDTO.getUser_id()));

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post not found with id: " + id));

        Comment parentComment = null;
        int hierarchy = 0; //db의 디폴트값 변경 예정

        if (commentCreateDTO.getParent_comment_id() != null) {
            //부모 댓글이 존재할 경우 부모 댓글 조회
            parentComment = commentRepository.findById(commentCreateDTO.getParent_comment_id())
                    .orElseThrow(() -> new IllegalArgumentException("Comment not found with id:" + commentCreateDTO.getParent_comment_id()));
            hierarchy = parentComment.getHierarchy() + 1; // 부모 댓글의 계층보다 1 증가
        }

        Comment comment = Comment.builder()
                .user(user)
                .post(post)
                .parentComment(parentComment)
                .comment_desc(commentCreateDTO.getComment_desc())
                .hierarchy(hierarchy)
                .createdAt(LocalDateTime.now())
                .build();
        return commentRepository.save(comment);
    }
}
