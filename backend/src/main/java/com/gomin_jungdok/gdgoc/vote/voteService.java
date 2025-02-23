package com.gomin_jungdok.gdgoc.vote;

import com.gomin_jungdok.gdgoc.post.Post;
import com.gomin_jungdok.gdgoc.post.PostRepository;
import com.gomin_jungdok.gdgoc.user.User;
import com.gomin_jungdok.gdgoc.user.UserRepository;
import com.gomin_jungdok.gdgoc.vote_option.VoteOption;
import com.gomin_jungdok.gdgoc.vote_option.VoteOptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class voteService {
    private final VoteRepository voteRepository;
    private final VoteOptionRepository voteOptionRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public Vote saveVote(int id, VoteDTO voteDTO) {
        // 게시글 존재 여부 검사
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다: " + id));

        // 선택지 존재 여부 검사
        VoteOption voteOption = voteOptionRepository.findById(voteDTO.getOption_id())
                .orElseThrow(() -> new IllegalArgumentException("잘못된 옵션입니다." + voteDTO.getOption_id()));

        // 해당 option_id가 post에 속하는지 검사
        if (!voteOption.getPost().equals(post)) {
            throw new IllegalArgumentException("선택한 옵션이 해당 게시글에 속하지 않습니다.");
        }

        // 사용자 존재 여부 검사
        User user = userRepository.findById(voteDTO.getVote_user())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 중복 투표 방지 (해당 사용자가 동일한 post에 대해 투표했는지 확인)
        if (voteRepository.existsByPostAndUser(post, user)) {
            throw new IllegalArgumentException("이미 투표한 게시글입니다.");
        }

        Vote vote = Vote.builder()
                .voteOption(voteOption)
                .post(post)
                .user(user)
                .build();

        return voteRepository.save(vote);
    }
}
