package com.example.CloudAPI.Likes;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class LikesId implements Serializable {
    private Long postId;
    private Long userId;

    public LikesId() {}

    public LikesId(Long postId, Long userId) {
        this.postId = postId;
        this.userId = userId;
    }

    // Getters and Setters
    public Long getPostId() {
        return postId;
    }
    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    // hashCode and equals
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LikesId likesId = (LikesId) o;
        return Objects.equals(postId, likesId.postId) && Objects.equals(userId, likesId.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(postId, userId);
    }
}
