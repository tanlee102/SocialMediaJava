package com.example.CloudAPI.Conversation;

import com.example.CloudAPI.Message.MessageDTO;
import com.example.CloudAPI.UserProfiles.UserProfileDTO;

import java.time.LocalDateTime;

public class ConversationDTO {
    private Long id;
    private UserProfileDTO otherUser;
    private LocalDateTime createdAt;
    private MessageDTO lastMessage;
    private boolean readAllMessages;
    private boolean blocked;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserProfileDTO getOtherUser() {
        return otherUser;
    }

    public void setOtherUser(UserProfileDTO otherUser) {
        this.otherUser = otherUser;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public MessageDTO getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(MessageDTO lastMessage) {
        this.lastMessage = lastMessage;
    }

    public boolean isReadAllMessages() {
        return readAllMessages;
    }

    public void setReadAllMessages(boolean readAllMessages) {
        this.readAllMessages = readAllMessages;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }
}
