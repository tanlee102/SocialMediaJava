package com.example.CloudAPI.Medias;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/media")
public class MediaController{

    @Autowired
    private MediaService mediaService;


    @GetMapping("/post/{postId}")
    public ResponseEntity<List<Media>> getMediaByPostId(@PathVariable Long postId) {
        List<Media> mediaList = mediaService.getMediaByPostId(postId);
        return ResponseEntity.ok(mediaList);
    }

    @PostMapping("/create")
    public ResponseEntity<Media> createMedia(@RequestBody MediaRequest mediaRequest) {
        try {
            Media createdMedia = mediaService.createMedia(mediaRequest);
            return new ResponseEntity<>(createdMedia, HttpStatus.CREATED);
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
}


//@PostMapping("/post/{postId}")
//public ResponseEntity<String> addPostMedia(@PathVariable Long postId, @RequestBody Media mediaRequest) {
//    boolean success = mediaService.addPostMedia(postId, mediaRequest.getContent(), mediaRequest.getUrl(), mediaRequest.getMediaType());
//    if (success) {
//        return ResponseEntity.ok("Added");
//    } else {
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body("Failed");
//    }
//}