package com.example.CloudAPI.Users.model;

public class UserDTO {
    private Long id;
    private String username;
    private String profileUrl;

    public UserDTO(Long id, String username, String profileUrl) {
        this.id = id;
        this.username = username;
        this.profileUrl = profileUrl;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }
}