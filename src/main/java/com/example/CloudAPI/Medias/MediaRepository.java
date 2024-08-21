package com.example.CloudAPI.Medias;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MediaRepository extends JpaRepository<Media, Long> {
    List<Media> findByPostId(Long postId);
}
