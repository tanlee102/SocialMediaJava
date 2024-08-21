package com.example.CloudAPI.Posts;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PostMapper {
    @Mapping(target = "likeCount", expression = "java(post.getLikes().size())")
    @Mapping(target = "commentCount", expression = "java(post.getComments().size())")
    @Mapping(target = "media", expression = "java(post.getMedias())")
    @Mapping(target = "restricted", source = "restricted")
    @Mapping(target = "muted", source = "muted")
    @Mapping(target = "viewCount", source = "viewCount")
    PostDTO toPostDTO(Post post);
}