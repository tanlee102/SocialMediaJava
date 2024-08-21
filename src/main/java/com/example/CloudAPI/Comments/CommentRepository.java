package com.example.CloudAPI.Comments;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId AND c.parent IS NULL AND c.id > :lastId ORDER BY c.createdAt ASC")
    Page<Comment> findRootCommentsAfterId(Long postId, Long lastId, Pageable pageable);

    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId AND c.parent IS NULL AND c.id < :firstId ORDER BY c.createdAt DESC")
    Page<Comment> findRootCommentsBeforeId(Long postId, Long firstId, Pageable pageable);

    @Query("SELECT c FROM Comment c WHERE c.parent.id = :parentId AND c.id > :lastId ORDER BY c.createdAt ASC")
    Page<Comment> findRepliesAfterId(Long parentId, Long lastId, Pageable pageable);

    @Query("SELECT c FROM Comment c WHERE c.parent.id = :parentId AND c.id < :firstId ORDER BY c.createdAt DESC")
    Page<Comment> findRepliesBeforeId(Long parentId, Long firstId, Pageable pageable);

    @Query("SELECT c.parent.id, COUNT(c) FROM Comment c WHERE c.parent.id IN :parentIds GROUP BY c.parent.id")
    List<Object[]> countRepliesByParentIds(List<Long> parentIds);
}