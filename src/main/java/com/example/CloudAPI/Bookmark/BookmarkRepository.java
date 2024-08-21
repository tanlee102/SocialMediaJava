package com.example.CloudAPI.Bookmark;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    List<Bookmark> findByUserId(Long userId);
    void deleteByUserIdAndPostId(Long userId, Long postId);
    boolean existsByUserIdAndPostId(Long userId, Long postId);

    @Query("SELECT b FROM Bookmark b WHERE b.user.id = :userId AND b.id < :id ORDER BY b.id DESC")
    List<Bookmark> findByUserIdAndIdLessThanOrderByIdDesc(Long userId, Long id, Pageable pageable);

    @Query("SELECT b FROM Bookmark b WHERE b.user.id = :userId ORDER BY b.id DESC")
    List<Bookmark> findByUserIdOrderByIdDesc(Long userId, Pageable pageable);
}