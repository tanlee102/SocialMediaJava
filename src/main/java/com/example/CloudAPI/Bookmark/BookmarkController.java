package com.example.CloudAPI.Bookmark;

import com.example.CloudAPI.Posts.Post;
import com.example.CloudAPI.Posts.PostDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/bookmarks")
@CrossOrigin(origins = "*")
public class BookmarkController {

    @Autowired
    private BookmarkService bookmarkService;

    @PostMapping("/toggle")
    public ResponseEntity<Bookmark> toggleBookmark(HttpServletRequest request, @RequestParam Long postId) {
        Long userId = Long.parseLong(request.getAttribute("userId").toString());
        Bookmark bookmark = bookmarkService.toggleBookmark(userId, postId);
        return ResponseEntity.ok(bookmark);
    }

    @GetMapping("/user")
    public ResponseEntity<List<PostDTO>> getBookmarksByUser(HttpServletRequest request) {
        Long userId = Long.parseLong(request.getAttribute("userId").toString());
        List<PostDTO> bookmarkedPosts = bookmarkService.getBookmarksByUser(userId);
        return ResponseEntity.ok(bookmarkedPosts);
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<Post> getPostById(@PathVariable Long postId) {
        Optional<Post> post = bookmarkService.getPostById(postId);
        return post.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<List<BookmarkPostDTO>> getBookmarksByUser(@PathVariable Long userId,
                                                            @RequestParam(required = false) Long id,
                                                            @RequestParam int limit) {
        List<BookmarkPostDTO> bookmarkedPosts = bookmarkService.getBookmarksByUser(userId, id, limit);
        return ResponseEntity.ok(bookmarkedPosts);
    }
}
