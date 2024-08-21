package com.example.CloudAPI.Tags;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TagService {

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private TagMapper tagMapper;


    public List<TagDTO> getTopTags() {
        return tagRepository.findAll().stream()
                .map(tagMapper::toTagDTO)
                .sorted((t1, t2) -> Long.compare(t2.getPostCount(), t1.getPostCount()))
                .limit(10)
                .collect(Collectors.toList());
    }
}