package com.example.CloudAPI.Comments;

import java.time.LocalDateTime;
import java.util.List;

public class CommentDTO {
    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private List<CommentDTO> replies;
    private int replyCount; // Add this field

    private UserCommentDTO userCommentDTO;
    private Long parentId;

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public UserCommentDTO getUserCommentDTO() {
        return userCommentDTO;
    }

    public void setUserCommentDTO(UserCommentDTO userCommentDTO) {
        this.userCommentDTO = userCommentDTO;
    }

    public int getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(int replyCount) {
        this.replyCount = replyCount;
    }

    public List<CommentDTO> getReplies() {
        return replies;
    }

    public void setReplies(List<CommentDTO> replies) {
        this.replies = replies;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}