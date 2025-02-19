package com.gomin_jungdok.gdgoc.post;

import com.gomin_jungdok.gdgoc.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
}