package com.example.CloudAPI.Followers;

public class UserFollowDTO {
    private Long userId;
    private String username;
    private String profileImageUrl;
    private boolean followed;

    // Constructors, getters, and setters

    public UserFollowDTO() {}

    public UserFollowDTO(Long userId, String username, String profileImageUrl, boolean followed) {
        this.userId = userId;
        this.username = username;
        this.profileImageUrl = profileImageUrl;
        this.followed = followed;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public boolean isFollowed() {
        return followed;
    }

    public void setFollowed(boolean followed) {
        this.followed = followed;
    }
}