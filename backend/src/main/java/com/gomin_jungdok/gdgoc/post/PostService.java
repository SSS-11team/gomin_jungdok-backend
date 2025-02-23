package com.gomin_jungdok.gdgoc.post;

import com.gomin_jungdok.gdgoc.post.dto.PostDetailResponseDto;
import com.gomin_jungdok.gdgoc.post.dto.PostWriteRequestDto;
import com.gomin_jungdok.gdgoc.post.post_image.PostImage;
import com.gomin_jungdok.gdgoc.post.post_image.PostImageRepository;
import com.gomin_jungdok.gdgoc.post.post_image.PostImageService;
import com.gomin_jungdok.gdgoc.user.User;
import com.gomin_jungdok.gdgoc.user.UserRepository;
import com.gomin_jungdok.gdgoc.vote.VoteRepository;
import com.gomin_jungdok.gdgoc.vote_option.VoteOption;
import com.gomin_jungdok.gdgoc.vote_option.VoteOptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;
    private final VoteOptionRepository voteOptionRepository;
    private final PostImageService postImageService;
    private final UserRepository userRepository;
    private final VoteRepository voteRepository;

    @Transactional
    public void createPost(PostWriteRequestDto requestDto) throws IOException {
        Post post = new Post();
        //TODO 로그인 구현 후 token에서 userId 추출해서 setUserId에 사용하도록 수정해야함
        post.setUserId(1L);
        post.setTitle(requestDto.getTitle());
        post.setDescription(requestDto.getDescription());

        post = postRepository.save(post);

        VoteOption option1 = new VoteOption(null, post, 1, requestDto.getOption1());
        VoteOption option2 = new VoteOption(null, post, 2, requestDto.getOption2());
        voteOptionRepository.saveAll(List.of(option1, option2));

        /*
        TODO 이미지 저장이 GCP 401 권한문제로 잘 작동하지 않음 확인 필요
        // 이미지 저장
        postImageService.uploadPostImages(requestDto.getImages(), post);
        */
    }

    public PostDetailResponseDto getPostDetail(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 게시글"));

        User writer = userRepository.findById(post.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 작성자"));

        //TODO 로그인 구현 후 token에서 userId 추출해서 currentUserId에 사용하도록 수정해야함
        Long currentUserId = 1L;
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 접속 유저"));

        boolean isMine = (post.getUserId().equals(currentUserId));
        boolean isVoted = (voteRepository.existsByVoteUserAndPostId(currentUser, postId));

        List<VoteOption> voteOptions = voteOptionRepository.findByPostId(postId);
        String option1Content = null;
        String option2Content = null;
        Long option1Votes = 0L;
        Long option2Votes = 0L;

        for (VoteOption option : voteOptions) {
            if (option.getOrder() == 1) {
                option1Content = option.getText();
                option1Votes = voteRepository.countByVoteOptionId(option.getId());
            } else if (option.getOrder() == 2) {
                option2Content = option.getText();
                option2Votes = voteRepository.countByVoteOptionId(option.getId());
            }
        }

        //TODO 100%를 맞추기 위해 100에서 옵션1 비율을 빼서 옵션2 비율을 계산중 더 정확하고 효율적인 방법 생각해보기
        Long totalVotes = option1Votes + option2Votes;
        String option1Percentage = (totalVotes > 0) ? (option1Votes * 100 / totalVotes) + "%" : "0%";
        String option2Percentage = (totalVotes > 0) ? 100 - (option1Votes * 100 / totalVotes) + "%" : "0%";

        List<PostImage> images = postImageRepository.findByPostId(post.getId());
        Map<String, String> imageUrls = new HashMap<>();
        for (int i = 0; i < images.size(); i++) {
            imageUrls.put("image" + (i + 1), images.get(i).getImageUrl());
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd, HH:mm");
        String formattedDate = dateFormat.format(post.getCreatedAt());
        
        return new PostDetailResponseDto(
                isVoted, isMine, post.isAI(), writer.getProfileImage(),
                writer.getNickname(), formattedDate, post.getTitle(), post.getDescription(),
                imageUrls, option1Content, option2Content,
                (isVoted || isMine) ? option1Votes : null,
                (isVoted || isMine) ? option2Votes : null,
                (isVoted || isMine) ? option1Percentage : null,
                (isVoted || isMine) ? option2Percentage : null
        );
    }
}
