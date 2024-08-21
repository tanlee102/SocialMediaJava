package com.example.CloudAPI.Comments;

import com.example.CloudAPI.Block.BlockId;
import com.example.CloudAPI.Block.BlockRepository;
import com.example.CloudAPI.Notifications.Notification;
import com.example.CloudAPI.Notifications.NotificationRepository;
import com.example.CloudAPI.Notifications.NotificationService;
import com.example.CloudAPI.Posts.Post;
import com.example.CloudAPI.Posts.PostRepository;
import com.example.CloudAPI.Users.model.User;
import com.example.CloudAPI.Users.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private BlockRepository blockRepository; // Add BlockRepository

    @Transactional
    public CommentDTO addComment(Long postId, Long userId, String content) {
        try {
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new RuntimeException("Post not found"));
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Check if the post owner has blocked the user
            if (isUserBlocked(post.getUser().getId(), userId)) {
                throw new RuntimeException("You are blocked from commenting on this post");
            }

            if (post.isMuted()) {
                throw new RuntimeException("You cannot comment on this post");
            }

            Comment comment = new Comment();
            comment.setPost(post);
            comment.setUser(user);
            comment.setContent(content);
            comment.setCreatedAt(LocalDateTime.now());

            Comment recomment = commentRepository.save(comment);

            // Use the new method in NotificationService
            notificationService.createNotification(post.getUser(), user.getId(), post.getId(), "comment");

            return convertToDto(recomment);
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    @Transactional
    public CommentDTO addReply(Long postId, Long userId, Long parentId, String content) {
        try {
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new RuntimeException("Post not found"));
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Comment parentComment = commentRepository.findById(parentId)
                    .orElseThrow(() -> new RuntimeException("Parent comment not found"));

            // Check if the post owner or parent comment owner has blocked the user
            if (isUserBlocked(post.getUser().getId(), userId) || isUserBlocked(parentComment.getUser().getId(), userId)) {
                throw new RuntimeException("You are blocked from replying to this comment");
            }

            if (parentComment.getParent() != null) {
                throw new RuntimeException("Replies can only be added to root comments");
            }

            Comment reply = new Comment();
            reply.setPost(post);
            reply.setUser(user);
            reply.setParent(parentComment);
            reply.setContent(content);
            reply.setCreatedAt(LocalDateTime.now());

            Comment recomment = commentRepository.save(reply);

            notificationService.createNotification(parentComment.getUser(), user.getId(), post.getId(), "reply");

            return convertToDto(recomment);
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
    public List<CommentDTO> getRootCommentsAfterId(Long postId, Long lastId, int limit) {
        Page<Comment> comments = commentRepository.findRootCommentsAfterId(postId, lastId, PageRequest.of(0, limit));
        return convertToDtoWithReplyCount(comments.getContent());
    }

    public List<CommentDTO> getRootCommentsBeforeId(Long postId, Long firstId, int limit) {
        Page<Comment> comments = commentRepository.findRootCommentsBeforeId(postId, firstId, PageRequest.of(0, limit));
        return convertToDtoWithReplyCount(comments.getContent());
    }

    public List<CommentDTO> getRepliesAfterId(Long parentId, Long lastId, int limit) {
        Page<Comment> replies = commentRepository.findRepliesAfterId(parentId, lastId, PageRequest.of(0, limit));
        return replies.stream().map(this::convertToDto).collect(Collectors.toList());
    }

//    public List<CommentDTO> getRepliesBeforeId(Long parentId, Long firstId, int limit) {
//        Page<Comment> replies = commentRepository.findRepliesBeforeId(parentId, firstId, PageRequest.of(0, limit));
//        return replies.stream().map(this::convertToDto).collect(Collectors.toList());
//    }

    private List<CommentDTO> convertToDtoWithReplyCount(List<Comment> comments) {
        List<Long> commentIds = comments.stream().map(Comment::getId).collect(Collectors.toList());
        Map<Long, Long> replyCounts = commentRepository.countRepliesByParentIds(commentIds).stream()
                .collect(Collectors.toMap(
                        result -> (Long) result[0],
                        result -> (Long) result[1]
                ));

        return comments.stream()
                .map(comment -> {
                    CommentDTO dto = convertToDto(comment);
                    dto.setReplyCount(replyCounts.getOrDefault(comment.getId(), 0L).intValue());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private CommentDTO convertToDto(Comment comment) {
        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setUserCommentDTO(convertUserToDto(comment.getUser()));
        dto.setContent(comment.getContent());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setParentId(comment.getParent() != null ? comment.getParent().getId() : null);
        return dto;
    }

    private UserCommentDTO convertUserToDto(User user) {
        UserCommentDTO dto = new UserCommentDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setProfileUrl(user.getUserProfile().getProfileImageUrl());
        return dto;
    }


    public boolean deleteById(Long id) {
        try{
            commentRepository.deleteById(id);
            return true;
        }catch (Exception e){
            throw new RuntimeException();
        }
    }


    private boolean isUserBlocked(Long blockerId, Long blockedId) {
        return blockRepository.existsById(new BlockId(blockerId, blockedId));
    }

}