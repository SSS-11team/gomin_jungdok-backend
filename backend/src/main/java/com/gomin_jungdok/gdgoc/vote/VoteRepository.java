package com.gomin_jungdok.gdgoc.vote;

import com.gomin_jungdok.gdgoc.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    boolean existsByVoteUserAndPostId(User voteUser, Long postId);

    Long countByVoteOptionId(Long id);
}