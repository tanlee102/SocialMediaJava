package com.example.CloudAPI.Posts;

import com.example.CloudAPI.Medias.Media;

import java.time.LocalDateTime;
import java.util.List;

public class PostDTO {
    private Long id;
    private String title;
    private LocalDateTime createdAt;
    private int likeCount;
    private int commentCount;
    private List<Media> media;
    private boolean restricted;
    private boolean muted;
    private int viewCount;
    private boolean liked;

    // Getters and setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public int getLikeCount() {
        return likeCount;
    }
    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getCommentCount() {
        return commentCount;
    }
    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public List<Media> getMedia() {
        return media;
    }
    public void setMedia(List<Media> media) {
        this.media = media;
    }

    public boolean isRestricted() {
        return restricted;
    }
    public void setRestricted(boolean restricted) {
        this.restricted = restricted;
    }

    public boolean isMuted() {
        return muted;
    }
    public void setMuted(boolean muted) {
        this.muted = muted;
    }

    public int getViewCount() {
        return viewCount;
    }
    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public boolean isLiked() {
        return liked;
    }
    public void setLiked(boolean liked) {
        this.liked = liked;
    }
}
