package com.example.CloudAPI.Users.model;

import com.example.CloudAPI.Block.Block;
import com.example.CloudAPI.Followers.Follower;
import com.example.CloudAPI.Likes.Like;
import com.example.CloudAPI.Posts.Post;
import com.example.CloudAPI.Role.Role;
import com.example.CloudAPI.UserProfiles.UserProfile;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@JsonIgnoreProperties({"likes", "following", "followers", "posts", "password", "email", "blocksInitiated", "blocksReceived"})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = true, unique = true, length = 40)
    private String username;

    @Column(name = "email", nullable = false, unique = true, length = 90)
    private String email;

    @Column(name = "password", nullable = false, length = 150)
    private String password;

    @Column(name = "verified", nullable = false)
    private boolean verified;

    @Column(name = "banned", nullable = true)
    private boolean banned;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("user")
    private UserProfile userProfile;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Follower> followers;

    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL)
    private Set<Follower> following;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Post> posts;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Like> likes;

    @ManyToOne
    @JoinColumn(name = "role_id", referencedColumnName = "id")
    private Role role;

    @OneToMany(mappedBy = "blocker", cascade = CascadeType.ALL)
    private Set<Block> blocksInitiated;

    @OneToMany(mappedBy = "blocked", cascade = CascadeType.ALL)
    private Set<Block> blocksReceived;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.banned = false;
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

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }
    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isVerified() {
        return verified;
    }
    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public boolean isBanned() {
        return banned;
    }
    public void setBanned(boolean banned) {
        this.banned = banned;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }
    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    public Set<Follower> getFollowers() {
        return followers;
    }
    public void setFollowers(Set<Follower> followers) {
        this.followers = followers;
    }

    public Set<Follower> getFollowing() {
        return following;
    }
    public void setFollowing(Set<Follower> following) {
        this.following = following;
    }

    public List<Post> getPosts() {
        return posts;
    }
    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public List<Like> getLikes() {
        return likes;
    }
    public void setLikes(List<Like> likes) {
        this.likes = likes;
    }

    public Set<Block> getBlocksInitiated() {
        return blocksInitiated;
    }

    public void setBlocksInitiated(Set<Block> blocksInitiated) {
        this.blocksInitiated = blocksInitiated;
    }

    public Set<Block> getBlocksReceived() {
        return blocksReceived;
    }

    public void setBlocksReceived(Set<Block> blocksReceived) {
        this.blocksReceived = blocksReceived;
    }
}