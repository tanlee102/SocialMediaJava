package com.example.CloudAPI.Comments;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/comments")
@CrossOrigin(origins = "*")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/post/{postId}")
    public ResponseEntity<CommentDTO> addComment(@PathVariable Long postId,
                                             @RequestBody CommentDTO commentDTO,
                                             HttpServletRequest request
    ) {
        Long userId = Long.parseLong(request.getAttribute("userId").toString());
        return ResponseEntity.ok(commentService.addComment(postId, userId, commentDTO.getContent()));
    }

    @PostMapping("/reply/post/{postId}/parent/{parentId}")
    public ResponseEntity<CommentDTO> addReply(@PathVariable Long postId,
                                           @PathVariable Long parentId,
                                           @RequestBody CommentDTO commentDTO,
                                           HttpServletRequest request
    ) {
        Long userId = Long.parseLong(request.getAttribute("userId").toString());
        return ResponseEntity.ok(commentService.addReply(postId, userId, parentId, commentDTO.getContent()));
    }

    @GetMapping("/post/{postId}/after/{lastId}")
    public ResponseEntity<List<CommentDTO>> getRootCommentsAfterId(@PathVariable Long postId,
                                                                   @PathVariable Long lastId,
                                                                   @RequestParam int limit) {
        List<CommentDTO> comments = commentService.getRootCommentsAfterId(postId, lastId, limit);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/post/{postId}/before/{firstId}")
    public ResponseEntity<List<CommentDTO>> getRootCommentsBeforeId(@PathVariable Long postId,
                                                                    @PathVariable Long firstId,
                                                                    @RequestParam int limit) {
        List<CommentDTO> comments = commentService.getRootCommentsBeforeId(postId, firstId, limit);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/{parentId}/replies/after/{lastId}")
    public ResponseEntity<List<CommentDTO>> getRepliesAfterId(@PathVariable Long parentId,
                                                              @PathVariable Long lastId,
                                                              @RequestParam int limit) {
        List<CommentDTO> replies = commentService.getRepliesAfterId(parentId, lastId, limit);
        return ResponseEntity.ok(replies);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteById(@PathVariable Long id) {
        commentService.deleteById(id);
        return ResponseEntity.ok("success");
    }

}