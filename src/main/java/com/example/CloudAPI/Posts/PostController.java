package com.example.CloudAPI.Posts;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/posts")
@CrossOrigin(origins = "*")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private PostMapper postMapper;

    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> getPostById(@PathVariable Long id) {
        Optional<Post> post = postService.getPostById(id);
        return post.map(p -> ResponseEntity.ok(postMapper.toPostDTO(p)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/v2/{postId}")
    public ItemPostDTO getPostByIdBeta(@PathVariable Long postId, @RequestParam(defaultValue = "-1") Long userId) {
        return postService.getPostByIdBeta(postId, userId);
    }

    @PostMapping("/create")
    public ResponseEntity<Map<String, Long>> createPost(HttpServletRequest request, @RequestBody Post post, @RequestParam Set<String> tags) {
        Long userId = Long.parseLong(request.getAttribute("userId").toString());
        Post rePost = postService.createPost(userId, post, tags);
        Map<String, Long> response = new HashMap<>();
        response.put("id", rePost.getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePost(@PathVariable Long id) {
        try {
            postService.deletePost(id);
            return ResponseEntity.ok("Removed");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed");
        }
    }

    @GetMapping("/users/{userId}")
    public List<PostDTO> getPostsByUserId(
            @PathVariable Long userId,
            @RequestParam(required = false) Long postId,
            @RequestParam int limit) {
        return postService.getPostsByUserId(userId, postId, limit);
    }

    @GetMapping("/orderById")
    public ResponseEntity<List<PostDTO>> getPostsOrderById(
            @RequestParam(required = false) Long postId,
            @RequestParam int limit,
            @RequestParam(required = false) Long userId) {
        List<PostDTO> posts = postService.getPostsOrderById(postId, limit, userId);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/orderByTotalLikes")
    public ResponseEntity<List<PostDTO>> getPostsOrderByTotalLikes(
            @RequestParam int pageNumber,
            @RequestParam int pageSize,
            @RequestParam(required = false) Long userId) {
        List<PostDTO> posts = postService.getPostsOrderByTotalLikes(pageNumber, pageSize, userId);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/topByTag")
    public ResponseEntity<List<PostDTO>> getTopPostsByTag(@RequestParam String tagName, @RequestParam int limit) {
        List<PostDTO> posts = postService.getTopPostsByTag(tagName, limit);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/topByTitle")
    public ResponseEntity<List<PostDTO>> getTopPostsByTitle(@RequestParam String keyword, @RequestParam int limit) {
        List<PostDTO> posts = postService.getTopPostsByTitle(keyword, limit);
        return ResponseEntity.ok(posts);
    }

    @PostMapping("/toggle-mute/{postId}")
    public ResponseEntity<PostDTO> toggleMuteStatus(@PathVariable Long postId) {
        Post post = postService.toggleMuteStatus(postId);
        return new ResponseEntity<>(postMapper.toPostDTO(post), HttpStatus.OK);
    }

    @PostMapping("/toggle-restrict/{postId}")
    public ResponseEntity<PostDTO> toggleRestrictStatus(@PathVariable Long postId) {
        Post post = postService.toggleRestrictStatus(postId);
        return new ResponseEntity<>(postMapper.toPostDTO(post), HttpStatus.OK);
    }

    // Add endpoints for fetching restricted and muted posts
    @GetMapping("/muted")
    public List<PostDTO> getMutedPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PostDTO> pageResult = postService.getMutedPosts(pageable);
        return pageResult.getContent(); // Return the content as a List
    }

    @GetMapping("/restricted")
    public List<PostDTO> getRestrictedPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PostDTO> pageResult = postService.getRestrictedPosts(pageable);
        return pageResult.getContent(); // Return the content as a List
    }

    @GetMapping("/random")
    public ResponseEntity<List<PostDTO>> getRandomPosts(@RequestParam int limit) {
        List<PostDTO> posts = postService.getRandomPosts(limit);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/followed")
    public ResponseEntity<List<PostDTO>> getPostsByFollowedUsers(HttpServletRequest request,
                                                                 @RequestParam int page,
                                                                 @RequestParam int size,
                                                                 @RequestParam Long userId) {
        Pageable pageable = PageRequest.of(page, size);
        List<PostDTO> posts = postService.getPostsByFollowedUsers(userId, pageable);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/randomExcludingId")
    public ResponseEntity<List<PostDTO>> getRandomPostsExcludingId(@RequestParam int limit, @RequestParam Long excludePostId) {
        List<PostDTO> posts = postService.getRandomPostsExcludingId(limit, excludePostId);
        return ResponseEntity.ok(posts);
    }
}