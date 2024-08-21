package com.example.CloudAPI.Tags;

public class TagDTO {

    private Long id;
    private String name;
    private Long postCount;

    public TagDTO() {}

    public TagDTO(Long id, String name, Long postCount) {
        this.id = id;
        this.name = name;
        this.postCount = postCount;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getPostCount() {
        return postCount;
    }

    public void setPostCount(Long postCount) {
        this.postCount = postCount;
    }
}
