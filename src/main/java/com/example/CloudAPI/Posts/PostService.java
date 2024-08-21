package com.example.CloudAPI.Posts;

import com.example.CloudAPI.Bookmark.BookmarkRepository;
import com.example.CloudAPI.Likes.LikeRepository;
import com.example.CloudAPI.Likes.LikesId;
import com.example.CloudAPI.Tags.Tag;
import com.example.CloudAPI.Tags.TagRepository;
import com.example.CloudAPI.Users.model.User;
import com.example.CloudAPI.Users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private ItemPostMapper itemPostMapper;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private BookmarkRepository bookmarkRepository;

    @Autowired
    private GetCommentPostMapper getCommentPostMapper;

    public Optional<Post> getPostById(Long id) {
        return postRepository.findById(id);
    }

    public Post createPost(Long userId, Post post, Set<String> tagNames) {
        try {
            Optional<User> userOptional = userRepository.findById(userId);
            if (userOptional.isPresent()) {
                post.setUser(userOptional.get());
                Set<Tag> tags = new HashSet<>();
                for (String tagName : tagNames) {
                    Tag tag = tagRepository.findByName(tagName).orElseGet(() -> {
                        Tag newTag = new Tag();
                        newTag.setName(tagName);
                        return tagRepository.save(newTag);
                    });
                    tags.add(tag);
                }
                post.setTags(tags);
                return postRepository.save(post);
            } else {
                throw new RuntimeException("User not found");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error creating");
        }
    }

    public void deletePost(Long id) {
        Optional<Post> postOptional = postRepository.findById(id);
        if (postOptional.isPresent()) {
            postRepository.deleteById(id);
        } else {
            throw new RuntimeException("Post not found");
        }
    }

    public List<PostDTO> getAllPosts() {
        return postRepository.findAll()
                .stream()
                .map(post -> {
                    PostDTO dto = postMapper.toPostDTO(post);
                    dto.setMedia(post.getMedias().size() > 0 ? List.of(post.getMedias().get(0)) : List.of());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<PostDTO> getPostsByUserId(Long userId, Long postId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Post> posts;
        if (postId != null) {
            posts = postRepository.findByUserIdAndIdLessThanOrderByIdDesc(userId, postId, pageable);
        } else {
            posts = postRepository.findByUserIdOrderByIdDesc(userId, pageable);
        }
        return posts.stream()
                .map(post -> {
                    PostDTO dto = postMapper.toPostDTO(post);
                    dto.setMedia(post.getMedias().size() > 0 ? List.of(post.getMedias().get(0)) : List.of());
                    return dto;
                })
                .collect(Collectors.toList());
    }


    public ItemPostDTO getPostByIdBeta(Long postId, Long userId) {
        return postRepository.findById(postId)
                .map(post -> {
                    post.setViewCount(post.getViewCount() + 1);
                    postRepository.save(post);
                    ItemPostDTO dto = itemPostMapper.toItemPostDTO(post);
                    LikesId likesId = new LikesId(postId, userId);
                    dto.setStatus(likeRepository.existsById(likesId));
                    dto.setBookmark(bookmarkRepository.existsByUserIdAndPostId(userId, postId));
                    dto.setMuted(post.isMuted());
                    dto.setRestricted(post.isRestricted());
                    return dto;
                })
                .orElseThrow(() -> new PostNotFoundException(postId));
    }



    public List<PostDTO> getPostsOrderById(Long postId, int limit, Long userId) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Post> posts;
        if (postId != null) {
            posts = postRepository.findPostsByIdLessThanOrderByIdDesc(postId, pageable);
        } else {
            posts = postRepository.findAllOrderByIdDesc(pageable);
        }
        return posts.stream()
                .map(post -> {
                    PostDTO dto = postMapper.toPostDTO(post);
                    dto.setMedia(post.getMedias().size() > 0 ? List.of(post.getMedias().get(0)) : List.of());
                    if (userId != null) {
                        LikesId likesId = new LikesId(post.getId(), userId);
                        dto.setLiked(likeRepository.existsById(likesId));
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<PostDTO> getPostsOrderByTotalLikes(int pageNumber, int pageSize, Long userId) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        List<Post> posts = postRepository.findAllOrderByTotalLikes(pageable);
        return posts.stream()
                .map(post -> {
                    PostDTO dto = postMapper.toPostDTO(post);
                    dto.setMedia(post.getMedias().size() > 0 ? List.of(post.getMedias().get(0)) : List.of());
                    if (userId != null) {
                        LikesId likesId = new LikesId(post.getId(), userId);
                        dto.setLiked(likeRepository.existsById(likesId));
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }



    public List<PostDTO> getTopPostsByTag(String tagName, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Post> posts = postRepository.findTopPostsByTag(tagName, pageable);
        return posts.stream()
                .map(post -> {
                    PostDTO dto = postMapper.toPostDTO(post);
                    dto.setMedia(post.getMedias().size() > 0 ? List.of(post.getMedias().get(0)) : List.of());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<PostDTO> getTopPostsByTitle(String keyword, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Post> posts = postRepository.findTopPostsByTitle(keyword, pageable);
        return posts.stream()
                .map(post -> {
                    PostDTO dto = postMapper.toPostDTO(post);
                    dto.setMedia(post.getMedias().size() > 0 ? List.of(post.getMedias().get(0)) : List.of());
                    return dto;
                })
                .collect(Collectors.toList());
    }



    public Post toggleMuteStatus(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
        post.setMuted(!post.isMuted());
        return postRepository.save(post);
    }

    public Post toggleRestrictStatus(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
        post.setRestricted(!post.isRestricted());
        return postRepository.save(post);
    }

    public Page<PostDTO> getMutedPosts(Pageable pageable) {
        return postRepository.findByMutedTrueOrderByIdDesc(pageable).map(postMapper::toPostDTO);
    }

    public Page<PostDTO> getRestrictedPosts(Pageable pageable) {
        return postRepository.findByRestrictedTrueOrderByIdDesc(pageable).map(postMapper::toPostDTO);
    }


    public List<PostDTO> getRandomPosts(int limit) {
        List<Post> posts = postRepository.findRandomPosts(limit);
        return posts.stream()
                .map(postMapper::toPostDTO)
                .collect(Collectors.toList());
    }

    public List<PostDTO> getPostsByFollowedUsers(Long userId, Pageable pageable) {
        List<Post> posts = postRepository.findPostsByFollowedUsers(userId, pageable);
        return posts.stream()
                .map(post -> {
                    PostDTO dto = postMapper.toPostDTO(post);
                    dto.setMedia(post.getMedias().size() > 0 ? List.of(post.getMedias().get(0)) : List.of());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<PostDTO> getRandomPostsExcludingId(int limit, Long excludePostId) {
        List<Post> posts = postRepository.findRandomPostsExcludingId(limit, excludePostId);
        return posts.stream()
                .map(postMapper::toPostDTO)
                .collect(Collectors.toList());
    }
}