package com.example.CloudAPI.Medias;

import com.example.CloudAPI.Posts.Post;
import com.example.CloudAPI.Posts.PostRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MediaService {

    @Autowired
    private MediaRepository mediaRepository;

    @Autowired
    private PostRepository postRepository;

    public List<Media> getMediaByPostId(Long postId) {
        return mediaRepository.findByPostId(postId);
    }

    public Media createMedia(MediaRequest mediaRequest) {
        Optional<Post> optionalPost = postRepository.findById(mediaRequest.getPostId());
        if (optionalPost.isEmpty()) {
            throw new RuntimeException("Post not found");
        }

        Media media = new Media();
        media.setContent(mediaRequest.getContent());
        media.setUrl(mediaRequest.getUrl());
        media.setThumb_url(mediaRequest.getThumbUrl());
        media.setMediaType(mediaRequest.getMediaType());
        media.setPost(optionalPost.get());

        return mediaRepository.save(media);
    }
}


//@Transactional
//public boolean addPostMedia(Long postId, String content, String url, String mediaType) {
//    try {
//        Optional<Post> postOptional = postRepository.findById(postId);
//        if (postOptional.isPresent()) {
//            Media media = new Media();
//            media.setPost(postOptional.get());
//            media.setContent(content);
//            media.setUrl(url);
//            media.setMediaType(mediaType);
//            mediaRepository.save(media);
//            return true;
//        } else {
//            throw new RuntimeException("Post not found");
//        }
//    } catch (Exception e) {
//        return false;
//    }
//}