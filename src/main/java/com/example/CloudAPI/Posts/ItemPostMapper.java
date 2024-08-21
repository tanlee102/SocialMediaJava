package com.example.CloudAPI.Posts;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ItemPostMapper {
    @Mapping(target = "user", expression = "java(post.getUser())")
    @Mapping(target = "likeCount", expression = "java(post.getLikes().size())")
    @Mapping(target = "commentCount", expression = "java(post.getComments().size())")
    @Mapping(target = "medias", expression = "java(post.getMedias())")
    @Mapping(target = "tags", expression = "java(post.getTags())")
    @Mapping(target = "viewCount", source = "viewCount")
    ItemPostDTO toItemPostDTO(Post post);
}