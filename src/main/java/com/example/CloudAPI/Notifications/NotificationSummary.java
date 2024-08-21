package com.example.CloudAPI.Notifications;

import java.time.LocalDateTime;
import java.util.List;

public class NotificationSummary {
    private String type;
    private Long entityId;
    private List<ActorDetails> actorDetails;
    private LocalDateTime latestCreatedAt;

    public NotificationSummary(String type, Long entityId, List<ActorDetails> actorDetails, LocalDateTime latestCreatedAt) {
        this.type = type;
        this.entityId = entityId;
        this.actorDetails = actorDetails;
        this.latestCreatedAt = latestCreatedAt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public List<ActorDetails> getActorDetails() {
        return actorDetails;
    }

    public void setActorDetails(List<ActorDetails> actorDetails) {
        this.actorDetails = actorDetails;
    }

    public LocalDateTime getLatestCreatedAt() {
        return latestCreatedAt;
    }

    public void setLatestCreatedAt(LocalDateTime latestCreatedAt) {
        this.latestCreatedAt = latestCreatedAt;
    }

    public static class ActorDetails {
        private Long actorId;
        private String username;
        private String profileImageUrl;

        public ActorDetails(Long actorId, String username, String profileImageUrl) {
            this.actorId = actorId;
            this.username = username;
            this.profileImageUrl = profileImageUrl;
        }

        public Long getActorId() {
            return actorId;
        }

        public void setActorId(Long actorId) {
            this.actorId = actorId;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getProfileImageUrl() {
            return profileImageUrl;
        }

        public void setProfileImageUrl(String profileImageUrl) {
            this.profileImageUrl = profileImageUrl;
        }
    }
}
