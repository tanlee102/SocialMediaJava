package com.example.CloudAPI.Posts;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("SELECT COUNT(p) FROM Post p WHERE p.user.id = :userId")
    long countByUserId(Long userId);

    @Query("SELECT p FROM Post p WHERE p.user.id = :userId AND p.id < :postId ORDER BY p.id DESC")
    List<Post> findByUserIdAndIdLessThanOrderByIdDesc(Long userId, Long postId, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.user.id = :userId ORDER BY p.id DESC")
    List<Post> findByUserIdOrderByIdDesc(Long userId, Pageable pageable);



    // Get posts ordered by creation date (latest post ID)
    @Query("SELECT p FROM Post p WHERE p.id < :postId ORDER BY p.id DESC")
    List<Post> findPostsByIdLessThanOrderByIdDesc(Long postId, Pageable pageable);

    @Query("SELECT p FROM Post p ORDER BY p.id DESC")
    List<Post> findAllOrderByIdDesc(Pageable pageable);

    // Get distinct posts ordered by latest comment
    @Query("SELECT p FROM Post p LEFT JOIN p.comments c WHERE c.id < :commentId GROUP BY p.id ORDER BY MAX(c.createdAt) DESC")
    List<Post> findDistinctPostsByLatestCommentId(Long commentId, Pageable pageable);

    @Query("SELECT p FROM Post p LEFT JOIN p.comments c GROUP BY p.id ORDER BY MAX(c.createdAt) DESC")
    List<Post> findAllDistinctOrderByLatestComment(Pageable pageable);

    // Get posts ordered by total likes
    @Query("SELECT p FROM Post p ORDER BY SIZE(p.likes) DESC, p.id DESC")
    List<Post> findAllOrderByTotalLikes(Pageable pageable);



    @Query("SELECT p FROM Post p JOIN p.tags t WHERE t.name = :tagName ORDER BY SIZE(p.likes) DESC")
    List<Post> findTopPostsByTag(String tagName, Pageable pageable);

//    @Query("SELECT p FROM Post p LEFT JOIN p.likes l WHERE p.title LIKE %:keyword% GROUP BY p ORDER BY COUNT(l) DESC")
//    List<Post> findTopPostsByTitle(String keyword, Pageable pageable);

    @Query("SELECT p FROM Post p LEFT JOIN p.likes l WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) GROUP BY p ORDER BY COUNT(l) DESC")
    List<Post> findTopPostsByTitle(@Param("keyword") String keyword, Pageable pageable);



    Page<Post> findByMutedTrueOrderByIdDesc(Pageable pageable);

    Page<Post> findByRestrictedTrueOrderByIdDesc(Pageable pageable);


    @Query(value = "SELECT * FROM posts ORDER BY RANDOM() LIMIT ?1", nativeQuery = true)
    List<Post> findRandomPosts(int limit);

    @Query("SELECT p FROM Post p WHERE p.user.id IN (SELECT f.id.followerId FROM Follower f WHERE f.id.userId = :userId) ORDER BY p.createdAt DESC")
    List<Post> findPostsByFollowedUsers(@Param("userId") Long userId, Pageable pageable);

    @Query(value = "SELECT * FROM posts WHERE id <> :excludePostId AND restricted = false ORDER BY RANDOM() LIMIT :limit", nativeQuery = true)
    List<Post> findRandomPostsExcludingId(@Param("limit") int limit, @Param("excludePostId") Long excludePostId);
}
