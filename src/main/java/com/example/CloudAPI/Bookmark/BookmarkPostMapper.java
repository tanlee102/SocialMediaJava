package com.example.CloudAPI.Bookmark;

import com.example.CloudAPI.Posts.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookmarkPostMapper {
    @Mapping(target = "likeCount", expression = "java(post.getLikes().size())")
    @Mapping(target = "commentCount", expression = "java(post.getComments().size())")
    @Mapping(target = "media", expression = "java(post.getMedias())")
    BookmarkPostDTO toBookmarkPostDTO(Post post);
}