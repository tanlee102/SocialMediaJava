package com.example.CloudAPI.Block;

import com.example.CloudAPI.Users.model.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "blocks")
public class Block {

    @EmbeddedId
    private BlockId id;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @ManyToOne
    @MapsId("blockerId")
    @JoinColumn(name = "blocker_id")
    private User blocker;

    @ManyToOne
    @MapsId("blockedId")
    @JoinColumn(name = "blocked_id")
    private User blocked;

    public Block() {}

    public Block(BlockId id, User blocker, User blocked) {
        this.id = id;
        this.blocker = blocker;
        this.blocked = blocked;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and setters

    public BlockId getId() {
        return id;
    }

    public void setId(BlockId id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getBlocker() {
        return blocker;
    }

    public void setBlocker(User blocker) {
        this.blocker = blocker;
    }

    public User getBlocked() {
        return blocked;
    }

    public void setBlocked(User blocked) {
        this.blocked = blocked;
    }
}
