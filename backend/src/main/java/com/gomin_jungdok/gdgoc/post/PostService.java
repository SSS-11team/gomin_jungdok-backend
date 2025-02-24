package com.gomin_jungdok.gdgoc.post;

import com.gomin_jungdok.gdgoc.post.dto.PostDetailResponseDto;
import com.gomin_jungdok.gdgoc.post.dto.PostListDetailResponseDto;
import com.gomin_jungdok.gdgoc.post.dto.PostListResponseDto;
import com.gomin_jungdok.gdgoc.post.dto.PostWriteRequestDto;
import com.gomin_jungdok.gdgoc.post.post_image.PostImage;
import com.gomin_jungdok.gdgoc.post.post_image.PostImageRepository;
import com.gomin_jungdok.gdgoc.post.post_image.PostImageService;
import com.gomin_jungdok.gdgoc.user.User;
import com.gomin_jungdok.gdgoc.user.UserRepository;
import com.gomin_jungdok.gdgoc.vote.VoteRepository;
import com.gomin_jungdok.gdgoc.vote.VoteUtils;
import com.gomin_jungdok.gdgoc.vote_option.VoteOption;
import com.gomin_jungdok.gdgoc.vote_option.VoteOptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        Map<String, Object> voteResult = VoteUtils.calculateVoteResults(voteOptions, voteRepository, isMine || isVoted);

        List<PostImage> images = postImageRepository.findByPostId(post.getId());
        Map<String, String> imageUrls = new HashMap<>();
        for (int i = 0; i < images.size(); i++) {
            imageUrls.put("image" + (i + 1), images.get(i).getImageUrl());
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd, HH:mm");
        String formattedDate = dateFormat.format(post.getCreatedAt());
        
        return new PostDetailResponseDto(
                isVoted,
                isMine,
                post.isAI(),
                writer.getProfileImage(),
                writer.getNickname(),
                formattedDate,
                post.getTitle(),
                post.getDescription(),
                imageUrls,
                (String) voteResult.get("option1Content"),
                (String) voteResult.get("option2Content"),
                (Long) voteResult.get("option1Votes"),
                (Long) voteResult.get("option2Votes"),
                (String) voteResult.get("option1Percentage"),
                (String) voteResult.get("option2Percentage")
        );
    }

    public PostListResponseDto getPosts(int size, Long lastId) {
        Pageable pageable = PageRequest.of(0, size);
        List<Post> posts = postRepository.findPostsAfterId(lastId, pageable);

        //TODO 로그인 구현 후 token에서 userId 추출해서 currentUserId에 사용하도록 수정해야함
        Long currentUserId = 1L;
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 접속 유저"));

        List<PostListDetailResponseDto> postListDetails = posts.stream().map(post -> {
            boolean isVoted = voteRepository.existsByVoteUserAndPostId(currentUser, post.getId());
            boolean isMine = post.getUserId().equals(currentUserId);
            boolean isAi = post.isAI();

            List<VoteOption> voteOptions = voteOptionRepository.findByPostId(post.getId());
            Map<String, Object> voteResult = VoteUtils.calculateVoteResults(voteOptions, voteRepository, isMine || isVoted);

            return new PostListDetailResponseDto(
                    post.getId(),
                    isVoted,
                    isMine,
                    isAi,
                    post.getTitle(),
                    (String) voteResult.get("option1Content"),
                    (String) voteResult.get("option2Content"),
                    (Long) voteResult.get("option1Votes"),
                    (Long) voteResult.get("option2Votes"),
                    (String) voteResult.get("option1Percentage"),
                    (String) voteResult.get("option2Percentage")
            );
        }).collect(Collectors.toList());

        return new PostListResponseDto(size, postListDetails);
    }
}
