package com.gomin_jungdok.gdgoc.vote;

import com.gomin_jungdok.gdgoc.vote_option.VoteOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

    long countByVoteOption(VoteOption option);

    int countByPostId(Long postId);

    int countByPostIdAndVoteOptionOrder(Long postId, int i);
}
