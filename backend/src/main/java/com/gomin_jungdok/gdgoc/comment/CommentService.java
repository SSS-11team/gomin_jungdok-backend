package com.gomin_jungdok.gdgoc.comment;

import com.gomin_jungdok.gdgoc.comment.dto.CommentCreateDTO;
import com.gomin_jungdok.gdgoc.post.Post;
import com.gomin_jungdok.gdgoc.post.PostRepository;
import com.gomin_jungdok.gdgoc.user.User;
import com.gomin_jungdok.gdgoc.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public void createComment(CommentCreateDTO requestDTO, long postId) {
        //TODO 로그인 구현 후 token에서 userId 추출해서 setUserId에 사용하도록 수정해야함
        Long userId = 1L;

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found with id: " + postId));

        Comment comment = new Comment();

        //부모 댓글이 존재할 경우 부모 댓글 조회
        if (requestDTO.getParentCommentId() != null) {
            Comment parentComment = null;
            parentComment = commentRepository.findById(requestDTO.getParentCommentId())
                    .orElseThrow(() -> new IllegalArgumentException("Comment not found with id:" + requestDTO.getParentCommentId()));
            int hierarchy = parentComment.getHierarchy() + 1; // 부모 댓글의 계층보다 1 증가

            comment.setParentComment(parentComment);
            comment.setHierarchy(hierarchy);
        }

        comment.setUser(user);
        comment.setPost(post);
        comment.setDescription(requestDTO.getDescription());

        commentRepository.save(comment);
    }
}
