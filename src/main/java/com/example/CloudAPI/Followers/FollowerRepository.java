package com.example.CloudAPI.Followers;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FollowerRepository extends JpaRepository<Follower, FollowerId> {
    void deleteById(FollowerId id);
    boolean existsById(FollowerId id);

    @Query("SELECT COUNT(f) FROM Follower f WHERE f.user.id = :userId")
    long countByUserId(Long userId);

    @Query("SELECT COUNT(f) FROM Follower f WHERE f.follower.id = :followerId")
    long countByFollowerId(Long followerId);

    @Query("SELECT f FROM Follower f WHERE f.follower.id = :userId ORDER BY f.createdAt DESC")
    List<Follower> findFollowersByUserId(Long userId);

    @Query("SELECT f FROM Follower f WHERE f.user.id = :userId ORDER BY f.createdAt DESC")
    List<Follower> findFollowingByUserId(Long userId);
}
