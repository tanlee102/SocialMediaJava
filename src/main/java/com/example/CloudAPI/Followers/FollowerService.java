package com.example.CloudAPI.Followers;

import com.example.CloudAPI.Notifications.Notification;
import com.example.CloudAPI.Notifications.NotificationRepository;
import com.example.CloudAPI.Notifications.NotificationService;
import com.example.CloudAPI.Users.model.User;
import com.example.CloudAPI.Users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FollowerService {

    @Autowired
    private FollowerRepository followerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    @Transactional
    public void followUser(Long userId, Long followerId) {
        if (userId.equals(followerId)) {
            throw new IllegalArgumentException("User cannot follow themselves");
        }
        FollowerId followerIdObj = new FollowerId(userId, followerId);
        if (followerRepository.existsById(followerIdObj)) {
            throw new IllegalArgumentException("Already following this user");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        User follower = userRepository.findById(followerId).orElseThrow(() -> new RuntimeException("Follower not found"));
        Follower followerEntity = new Follower(followerIdObj, user, follower);
        followerRepository.save(followerEntity);

        // Use the new method in NotificationService
        notificationService.createNotification(follower, user.getId(), user.getId(), "follow");
    }

    @Transactional
    public void unfollowUser(Long userId, Long followerId) {
        FollowerId id = new FollowerId(userId, followerId);
        if (!followerRepository.existsById(id)) {
            throw new IllegalArgumentException("Not following this user");
        }
        followerRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<UserFollowDTO> getFollowers(Long userId, Long currentUserId) {
        List<Follower> followers = followerRepository.findFollowersByUserId(userId);
        return followers.stream()
                .map(f -> {
                    User followerUser = f.getUser();
                    boolean followed = followerRepository.existsById(new FollowerId(currentUserId, followerUser.getId()));
                    return new UserFollowDTO(
                            followerUser.getId(),
                            followerUser.getUsername(),
                            followerUser.getUserProfile().getProfileImageUrl(),
                            followed
                    );
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserFollowDTO> getFollowing(Long userId, Long currentUserId) {
        List<Follower> following = followerRepository.findFollowingByUserId(userId);
        return following.stream()
                .map(f -> {
                    User followingUser = f.getFollower();
                    boolean followed = followerRepository.existsById(new FollowerId(currentUserId, followingUser.getId()));
                    return new UserFollowDTO(
                            followingUser.getId(),
                            followingUser.getUsername(),
                            followingUser.getUserProfile().getProfileImageUrl(),
                            followed
                    );
                })
                .collect(Collectors.toList());
    }
}
