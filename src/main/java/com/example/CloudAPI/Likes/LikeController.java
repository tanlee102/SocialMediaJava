package com.example.CloudAPI.Likes;

import com.example.CloudAPI.Posts.PostDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/likes")
@CrossOrigin(origins = "*")
public class LikeController {

    @Autowired
    private LikeService likeService;

    @PostMapping("/post/{postId}")
    public ResponseEntity<String> toggleLike(@PathVariable Long postId, HttpServletRequest request) {
        try {
            Long userId = Long.parseLong(request.getAttribute("userId").toString());
            Boolean likes = likeService.toggleLike(postId, userId);
            if (likes) {
                return ResponseEntity.ok("Added");
            } else {
                return ResponseEntity.ok("Removed");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while toggling like");
        }
    }

    @GetMapping("/status/post/{postId}")
    public ResponseEntity<String> hasUserLikedPost(@PathVariable Long postId, HttpServletRequest request) {
        try {
            Long userId = Long.parseLong(request.getAttribute("userId").toString());
            boolean hasLiked = likeService.hasUserLikedPost(postId, userId);
            if (hasLiked) {
                return ResponseEntity.ok("True");
            } else {
                return ResponseEntity.ok("False");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while checking like status");
        }
    }

    @GetMapping("/{postId}/count")
    public long getLikeCount(@PathVariable Long postId) {
        return likeService.getPostLikeCount(postId);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<List<PostDTO>> getLikesByUser(@PathVariable Long userId,
                                                        @RequestParam(required = false) String createdAt,
                                                        @RequestParam int limit) {
        LocalDateTime createdAtDateTime = null;
        if (createdAt != null) {
            createdAtDateTime = LocalDateTime.parse(createdAt);
        }
        List<PostDTO> likedPosts = likeService.getLikesByUser(userId, createdAtDateTime, limit);
        return ResponseEntity.ok(likedPosts);
    }

}