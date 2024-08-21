package com.example.CloudAPI.Bookmark;

import com.example.CloudAPI.Posts.Post;
import com.example.CloudAPI.Posts.PostDTO;
import com.example.CloudAPI.Posts.PostMapper;
import com.example.CloudAPI.Posts.PostRepository;
import com.example.CloudAPI.Users.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookmarkService {

    @Autowired
    private BookmarkRepository bookmarkRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private BookmarkPostMapper bookmarkPostMapper;

    @Transactional
    public Bookmark toggleBookmark(Long userId, Long postId) {
        if (!userRepository.existsById(userId) || !postRepository.existsById(postId)) {
            throw new RuntimeException("User or Post not found");
        }

        boolean isBookmarked = bookmarkRepository.existsByUserIdAndPostId(userId, postId);
        if (isBookmarked) {
            bookmarkRepository.deleteByUserIdAndPostId(userId, postId);
            return null; // Return null to indicate that the bookmark was removed
        } else {
            Bookmark bookmark = new Bookmark();
            bookmark.setUser(userRepository.findById(userId).get());
            bookmark.setPost(postRepository.findById(postId).get());
            return bookmarkRepository.save(bookmark);
        }
    }

    public List<PostDTO> getBookmarksByUser(Long userId) {
        return bookmarkRepository.findByUserId(userId).stream()
                .map(Bookmark::getPost)
                .map(post -> {
                    PostDTO dto = postMapper.toPostDTO(post);
                    dto.setMedia(post.getMedias().size() > 0 ? List.of(post.getMedias().get(0)) : List.of());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public Optional<Post> getPostById(Long postId) {
        return postRepository.findById(postId);
    }


    public List<BookmarkPostDTO> getBookmarksByUser(Long userId, Long id, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Bookmark> bookmarks;

        if (id != null) {
            bookmarks = bookmarkRepository.findByUserIdAndIdLessThanOrderByIdDesc(userId, id, pageable);
        } else {
            bookmarks = bookmarkRepository.findByUserIdOrderByIdDesc(userId, pageable);
        }

        return bookmarks.stream()
                .map(bookmark -> {
                    BookmarkPostDTO dto = bookmarkPostMapper.toBookmarkPostDTO(bookmark.getPost());
                    dto.setMedia(bookmark.getPost().getMedias().size() > 0 ? List.of(bookmark.getPost().getMedias().get(0)) : List.of());
                    dto.setBookmarkId(bookmark.getId());
                    return dto;
                })
                .collect(Collectors.toList());
    }

}
