package com.gomin_jungdok.gdgoc.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    //List<Comment> findByPostId(int post_id);
}