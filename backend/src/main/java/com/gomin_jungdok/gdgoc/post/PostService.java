package com.gomin_jungdok.gdgoc.post;

import com.gomin_jungdok.gdgoc.post.dto.PostWriteRequestDto;
import com.gomin_jungdok.gdgoc.post.dto.PostListDetailResponseDto;
import com.gomin_jungdok.gdgoc.post.dto.PostDetailResponseDto;
import com.gomin_jungdok.gdgoc.post.dto.PostListResponseDto;
import com.gomin_jungdok.gdgoc.post.dto.PostWriteRequestDto;
import com.gomin_jungdok.gdgoc.post.post_image.PostImage;
import com.gomin_jungdok.gdgoc.post.post_image.PostImageService;
import com.gomin_jungdok.gdgoc.post.post_image.PostImageRepository;
import com.gomin_jungdok.gdgoc.vote_option.VoteOption;
import com.gomin_jungdok.gdgoc.vote_option.VoteOptionRepository;
import com.gomin_jungdok.gdgoc.vote.VoteResultDTO;
import com.gomin_jungdok.gdgoc.user.User;
import com.gomin_jungdok.gdgoc.user.UserRepository;
import com.gomin_jungdok.gdgoc.vote.VoteUtils;
import com.gomin_jungdok.gdgoc.vote.VoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final VoteRepository voteRepository;
    private final VoteOptionRepository voteOptionRepository;
    private final PostImageService postImageService;
    private final PostImageRepository postImageRepository;

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

    public List<TodayPostsDTO> getTodayPost() {
        ZoneId koreaZone = ZoneId.of("Asia/Seoul");
        ZoneId utcZone = ZoneId.of("UTC");

        // 현재 시간을 UTC 기준으로 변환
        LocalDate todayInKorea = Instant.now().atZone(koreaZone).toLocalDate();

        // 오늘 00:00:00 ~ 23:59:59을 UTC 기준으로 변환
        LocalDateTime startTime = todayInKorea.atStartOfDay(koreaZone)
                .withZoneSameInstant(utcZone).toLocalDateTime();
        LocalDateTime endTime = startTime.plusDays(1).minusSeconds(1);

        System.out.println("UTC 기준 StartTime: " + startTime);
        System.out.println("UTC 기준 EndTime: " + endTime);

        List<Object[]> topVotedPosts = voteRepository.findTodayPosts(startTime, endTime);

        return topVotedPosts.stream()
                .map(obj -> {
                    long postId = (long) obj[0];
                    long voteCount = (long) obj[1];

                    Post post = postRepository.findById(postId)
                            .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다: " + postId));

                    return new TodayPostsDTO(
                            post.getId(),
                            post.getTitle(),
                            post.getDescription(),
                            voteCount
                    );
                })
                .collect(Collectors.toList());
    }


    //오늘의 고민 상세 조회
    public PostDetailDTO getPostDetail(long post_id) {
        Post post = postRepository.findById(post_id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다: " + post_id));

        // 이미지 리스트 조회
        List<String> imageUrls = postImageRepository.findByPost(post)
                .stream()
                .map(PostImage::getImageUrl)
                .collect(Collectors.toList());

        // 투표 결과 조회
        List<Object[]> voteData = voteRepository.findVoteResults(post_id);
        long totalVotes = voteData.stream()
                .mapToLong(v -> v[1] instanceof Number ? ((Number) v[1]).longValue() : 0)
                .sum(); // 총 투표 수 계산

        List<VoteResultDTO> voteResults = voteData.stream()
                .map(v -> {
                    String name = v[0] != null ? v[0].toString() : "Unknown"; // 안전한 변환
                    long votes = v[1] instanceof Number ? ((Number) v[1]).longValue() : 0; // 숫자 변환 처리
                    long percentage = totalVotes == 0 ? 0 : Math.round(((double) votes / totalVotes) * 100);
                    return new VoteResultDTO(name, percentage);
                })
                .collect(Collectors.toList());


/*
        // 댓글 조회
            List<CommentDTO> comments = commentRepository.findByPostId(post_id)
                .stream()
                .map(c -> new CommentDTO(
                        c.getUser().getUsername(),
                        c.getContent(),
                        c.getCreatedAt().toString()
                ))
                .collect(Collectors.toList());
*/
        return new PostDetailDTO(
                post.getId(),
                post.getTitle(),
                post.getDescription(),
                imageUrls,
                voteResults
                //comments
        );
    }
}
