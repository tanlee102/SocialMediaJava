package com.example.CloudAPI.Likes;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface LikeRepository extends JpaRepository<Like, LikesId> {

    void deleteById(LikesId likesId);

    @Query("SELECT COUNT(l) FROM Like l WHERE l.post.id = :postId")
    long countLikesByPostId(@Param("postId") Long postId);

    @Query("SELECT l FROM Like l WHERE l.user.id = :userId AND l.createdAt < :createdAt ORDER BY l.createdAt DESC")
    List<Like> findByUserIdAndCreatedAtLessThanOrderByCreatedAtDesc(Long userId, LocalDateTime createdAt, Pageable pageable);

    @Query("SELECT l FROM Like l WHERE l.user.id = :userId ORDER BY l.createdAt DESC")
    List<Like> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
}
