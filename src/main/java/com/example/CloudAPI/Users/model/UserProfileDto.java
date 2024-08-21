package com.example.CloudAPI.Users.model;

public class UserProfileDto {

    private Long id;
    private String username;
    private String email;
    private boolean verified;
    private String profileImageUrl;
    private String bio;
    private long followersCount;
    private long followingCount;
    private long postsCount;
    private boolean isFollowing;
    private boolean isBlocked;
    private boolean beBlocked;
    private boolean banned;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // Getters and setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public long getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(long followersCount) {
        this.followersCount = followersCount;
    }

    public long getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(long followingCount) {
        this.followingCount = followingCount;
    }

    public long getPostsCount() {
        return postsCount;
    }

    public void setPostsCount(long postsCount) {
        this.postsCount = postsCount;
    }

    public boolean isFollowing() {
        return isFollowing;
    }

    public void setIsFollowing(boolean following) {
        isFollowing = following;
    }

    public void setFollowing(boolean following) {
        isFollowing = following;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public boolean isBanned() {
        return banned;
    }

    public void setBanned(boolean banned) {
        this.banned = banned;
    }

    public boolean isBeBlocked() {
        return beBlocked;
    }

    public void setBeBlocked(boolean beBlocked) {
        this.beBlocked = beBlocked;
    }
}
