package com.example.CloudAPI.Tags;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TagMapper {

    @Mapping(target = "postCount", expression = "java((long) tag.getPosts().size())")
    TagDTO toTagDTO(Tag tag);
}