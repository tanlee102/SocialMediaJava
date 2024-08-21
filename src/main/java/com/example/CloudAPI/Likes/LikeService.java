package com.example.CloudAPI.Likes;

import com.example.CloudAPI.Notifications.Notification;
import com.example.CloudAPI.Notifications.NotificationRepository;
import com.example.CloudAPI.Notifications.NotificationService;
import com.example.CloudAPI.Posts.Post;
import com.example.CloudAPI.Posts.PostDTO;
import com.example.CloudAPI.Posts.PostMapper;
import com.example.CloudAPI.Posts.PostRepository;
import com.example.CloudAPI.Users.model.User;
import com.example.CloudAPI.Users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LikeService {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private NotificationService notificationService;

    public Boolean toggleLike(Long postId, Long userId) {
        LikesId likesId = new LikesId(postId, userId);

        Optional<Post> optionalPost = postRepository.findById(postId);
        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalPost.isPresent() && optionalUser.isPresent()) {
            Post post = optionalPost.get();
            User user = optionalUser.get();

            Optional<Like> existingLike = likeRepository.findById(likesId);
            if (existingLike.isPresent()) {
                likeRepository.deleteById(likesId);
                return false;
            } else {
                Like like = new Like();
                like.setId(likesId);
                like.setPost(post);
                like.setUser(user);
                try {
                    likeRepository.save(like);

                    notificationService.createNotification(post.getUser(), user.getId(), post.getId(), "like");

                    return true;
                } catch (Exception e) {
                    System.err.println("Error saving like: " + e.getMessage());
                    throw new RuntimeException("Failed to save like", e);
                }
            }
        } else {
            throw new IllegalArgumentException("Post or User not found");
        }
    }

    public boolean hasUserLikedPost(Long postId, Long userId) {
        LikesId likesId = new LikesId(postId, userId);
        return likeRepository.existsById(likesId);
    }

    public long getPostLikeCount(Long postId) {
        return likeRepository.countLikesByPostId(postId);
    }


    public List<PostDTO> getLikesByUser(Long userId, LocalDateTime createdAt, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Like> likes;

        if (createdAt != null) {
            likes = likeRepository.findByUserIdAndCreatedAtLessThanOrderByCreatedAtDesc(userId, createdAt, pageable);
        } else {
            likes = likeRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        }

        return likes.stream()
                .map(like -> {
                    PostDTO dto = postMapper.toPostDTO(like.getPost());
                    dto.setMedia(like.getPost().getMedias().size() > 0 ? List.of(like.getPost().getMedias().get(0)) : List.of());
                    dto.setCreatedAt(like.getCreatedAt()); // Set PostDTO createdAt to Like createdAt
                    return dto;
                })
                .collect(Collectors.toList());
    }
}