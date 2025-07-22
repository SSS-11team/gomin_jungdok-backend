package com.gomin_jungdok.gdgoc.comment;

import com.gomin_jungdok.gdgoc.post.Post;
import com.gomin_jungdok.gdgoc.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostAndDeletedAtIsNull(Post post);

    @Query("""
                SELECT c.post.id, COUNT(c)
                FROM Comment c
                WHERE c.post.id IN :todayPosts
                GROUP BY c.post.id
            """)
    List<Object[]> countCommentsByPostIds(@Param("todayPosts") List<Long> todayPosts);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.post.id = :post_id")
    Long countCommentsByPostId(@Param("post_id") Long post_id);

    long countByUserAndDeletedAtIsNotNull(User user);
}