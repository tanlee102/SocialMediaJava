package com.example.CloudAPI.Posts;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface GetCommentPostMapper {
    @Mapping(target = "likeCount", expression = "java(post.getLikes().size())")
    @Mapping(target = "commentCount", expression = "java(post.getComments().size())")
    @Mapping(target = "media", expression = "java(post.getMedias().size() > 0 ? List.of(post.getMedias().get(0)) : List.of())")
    @Mapping(target = "commentId", ignore = true) // We will set commentId manually
    GetCommentPostDTO toGetCommentPostDTO(Post post);

    void updateCommentId(@MappingTarget GetCommentPostDTO dto, Long commentId);
}
