package com.gomin_jungdok.gdgoc.vote;

import com.gomin_jungdok.gdgoc.post.Post;
import com.gomin_jungdok.gdgoc.post.PostRepository;
import com.gomin_jungdok.gdgoc.user.User;
import com.gomin_jungdok.gdgoc.user.UserRepository;
import com.gomin_jungdok.gdgoc.vote.DTO.VoteRequestDTO;
import com.gomin_jungdok.gdgoc.vote.DTO.VoteResponseDTO;
import com.gomin_jungdok.gdgoc.vote_option.VoteOption;
import com.gomin_jungdok.gdgoc.vote_option.VoteOptionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final VoteOptionRepository voteOptionRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    // 투표 기능

    // result : { "option1Percentage": "55%", "option2Percentage": "45%" }
    // VoteResponseDTO result = voteOptionService.vote((userId)1L, (postId)1L, voteRequest) // 기존 : 토큰에서 userid 가져옴

    @Transactional
    public VoteResponseDTO vote(Long vote_userId, Long postId, VoteRequestDTO voteRequest) {

        int voteOrder = voteRequest.getVote();  // (1 or 2)

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));


        VoteOption selectedOption = voteOptionRepository.findByPostAndOrder(post, voteOrder)
            .orElseThrow(() -> new RuntimeException("선택지가 존재하지 않습니다."));

        System.out.println("selectedOption.getId() = " + selectedOption.getId());

        User voter = userRepository.findById(vote_userId)
                .orElseThrow(() -> new RuntimeException("투표자를 찾을 수 없습니다."));
//        if (post.getUserId().equals(user.getUserId())) {
//            throw new RuntimeException("작성자는 투표할 수 없습니다.");
//        }

        Vote vote = new Vote();
        vote.setPost(post); // Post ID로 POST 찾아오기
        vote.setVoteOption(selectedOption);
        vote.setVoteUser(voter); // User ID로 투표한 사람 찾아오기
        voteRepository.save(vote);


        return voteResults(postId);
    }

    // post에서 order 당 투표 결과 반환
    public VoteResponseDTO voteResults(Long postId) {
        int totalVotes = voteRepository.countByPostId(postId);

        int option1Votes = voteRepository.countByPostIdAndVoteOptionOrder(postId, 1);
        int option2Votes = voteRepository.countByPostIdAndVoteOptionOrder(postId, 2);

        VoteResponseDTO result = new VoteResponseDTO();
        result.setVoteOfOption1(option1Votes);
        result.setVoteOfOption2(option2Votes);

        double percentageOfOption1 = (totalVotes == 0) ? 0 : ((double) option1Votes / (double) totalVotes) * 100;
        double percentageOfOption2 = (totalVotes == 0) ? 0 : ((double) option2Votes / (double) totalVotes) * 100;

        // 소수점 첫째 자리까지 반올림
        percentageOfOption1 = Math.round(percentageOfOption1 * 10.0) / 10.0;
        percentageOfOption2 = Math.round(percentageOfOption2 * 10.0) / 10.0;

        result.setOption1Percentage(percentageOfOption1);
        result.setOption2Percentage(percentageOfOption2);
        return result;
    }
}