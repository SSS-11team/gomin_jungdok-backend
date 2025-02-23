package com.gomin_jungdok.gdgoc.vote;

import com.gomin_jungdok.gdgoc.post.Post;
import com.gomin_jungdok.gdgoc.user.User;
import com.gomin_jungdok.gdgoc.vote.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface VoteRepository extends JpaRepository<Vote, Integer> {
    /*@Query("SELECT v.post.post_id, COUNT(v.vote_id) AS voteCount " +
            "FROM Vote v " +
            "WHERE v.createdAt BETWEEN :startOfDay AND :endOfDay " +
            "GROUP BY v.post.post_id " +
            "ORDER BY voteCount DESC " +
            "LIMIT 3")
    List<Object[]> findTodayPosts(LocalDateTime startOfDay, LocalDateTime endOfDay);*/
    @Query("SELECT v.post.post_id, COUNT(v.vote_id) AS voteCount " +
            "FROM Vote v " +
            "WHERE v.createdAt BETWEEN :startOfDay AND :endOfDay " +
            "GROUP BY v.post.post_id " +
            "ORDER BY voteCount DESC " +
            "LIMIT 3")
    List<Object[]> findTodayPosts(LocalDateTime startOfDay, LocalDateTime endOfDay);


    @Query("SELECT v.voteOption, COUNT(v) FROM Vote v WHERE v.post.post_id = :post_id GROUP BY v.voteOption")
    List<Object[]> findVoteResults(@Param("post_id") int post_id);

    // 중복 투표 방지
    boolean existsByPostAndUser(Post post, User user);
}
