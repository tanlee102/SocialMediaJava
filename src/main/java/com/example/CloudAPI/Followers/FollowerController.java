package com.example.CloudAPI.Followers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/followers")
@CrossOrigin(origins = "*")
public class FollowerController {
    @Autowired
    private FollowerService followerService;

    @PostMapping("/follow")
    public ResponseEntity<String> followUser(HttpServletRequest request, @RequestParam Long followerId) {
        try {
            Long userId = Long.parseLong(request.getAttribute("userId").toString());
            followerService.followUser(userId, followerId);
            return ResponseEntity.ok("Successfully followed the user");
        } catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/unfollow")
    public ResponseEntity<String> unfollowUser(HttpServletRequest request, @RequestParam Long followerId) {
        try {
            Long userId = Long.parseLong(request.getAttribute("userId").toString());
            followerService.unfollowUser(userId, followerId);
            return ResponseEntity.ok("Successfully unfollowed the user");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/followers")
    public ResponseEntity<List<UserFollowDTO>> getFollowers(HttpServletRequest request, @RequestParam Long userId) {
        try {
            Long currentUserId = Long.parseLong(request.getAttribute("userId").toString());
            List<UserFollowDTO> followers = followerService.getFollowers(userId, currentUserId);
            return ResponseEntity.ok(followers);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/following")
    public ResponseEntity<List<UserFollowDTO>> getFollowing(HttpServletRequest request, @RequestParam Long userId) {
        try {
            Long currentUserId = Long.parseLong(request.getAttribute("userId").toString());
            List<UserFollowDTO> following = followerService.getFollowing(userId, currentUserId);
            return ResponseEntity.ok(following);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
