package com.example.CloudAPI.Posts;

import com.example.CloudAPI.Medias.Media;
import java.util.List;

public class GetCommentPostDTO {
    private Long id;
    private Long commentId;
    private List<Media> media;
    private int likeCount;
    private int commentCount;
    // other fields and getters/setters

    // Constructor to set commentId
    public GetCommentPostDTO(Long id, Long commentId, List<Media> media, int likeCount, int commentCount) {
        this.id = id;
        this.commentId = commentId;
        this.media = media;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public List<Media> getMedia() {
        return media;
    }

    public void setMedia(List<Media> media) {
        this.media = media;
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
}
