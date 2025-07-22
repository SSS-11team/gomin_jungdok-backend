package com.gomin_jungdok.gdgoc.report;

import com.gomin_jungdok.gdgoc.comment.Comment;
import com.gomin_jungdok.gdgoc.comment.CommentRepository;
import com.gomin_jungdok.gdgoc.post.Post;
import com.gomin_jungdok.gdgoc.post.PostRepository;
import com.gomin_jungdok.gdgoc.report.dto.ReportRequestDto;
import com.gomin_jungdok.gdgoc.user.User;
import com.gomin_jungdok.gdgoc.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createReport(ReportRequestDto requestDto) {
        Report report = new Report();
        //TODO 로그인 구현 후 token에서 userId 추출해서 setUserId에 사용하도록 수정해야함
        report.setReporterId(2L);
        report.setTargetId(requestDto.getTargetId());
        report.setTargetType(Report.TargetType.valueOf(requestDto.getTargetType()));
        reportRepository.save(report);


        // 같은 게시글/댓글 신고 3회 누적일 시 소프트 딜리트
        long reportCount = reportRepository.countByTargetTypeAndTargetId(
                report.getTargetType(), report.getTargetId()
        );
        if (reportCount >= 3) {
            Long writer_id = null;

            if (report.getTargetType() == Report.TargetType.POST) {
                Optional<Post> postOpt = postRepository.findById(report.getTargetId());
                if (postOpt.isPresent()) {
                    Post post = postOpt.get();
                    post.setDeletedAt(new Date());
                    postRepository.save(post);
                    writer_id = post.getUserId();
                }
            } else if (report.getTargetType() == Report.TargetType.COMMENT) {
                Optional<Comment> commentOpt = commentRepository.findById(report.getTargetId());
                if (commentOpt.isPresent()) {
                    Comment comment = commentOpt.get();
                    comment.setDeletedAt(new Date());
                    commentRepository.save(comment);
                    writer_id = comment.getUser().getId();
                }
            }

            // 작성자의 soft delete 된 게시글+댓글 개수 3개 이상이면 정지
            if (writer_id != null) {
                User writer = userRepository.findById(writer_id).orElseThrow();
                long deletedPostCount = postRepository.countByUserIdAndDeletedAtIsNotNull(writer_id);
                long deletedCommentCount = commentRepository.countByUserAndDeletedAtIsNotNull(writer);
                long totalDeleted = deletedPostCount + deletedCommentCount;

                if (totalDeleted >= 3 && writer.getStatus() == User.UserStatus.ACTIVE) {
                    writer.setStatus(User.UserStatus.SUSPENDED);
                    userRepository.save(writer);
                }
            }
        }
    }
}
