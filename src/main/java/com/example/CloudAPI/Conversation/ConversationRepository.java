package com.example.CloudAPI.Conversation;

import com.example.CloudAPI.Users.model.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.List;

@EnableJpaRepositories
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    @Query("SELECT c FROM Conversation c WHERE (c.user1 = :user1 AND c.user2 = :user2) OR (c.user1 = :user2 AND c.user2 = :user1)")
    Conversation findByUsers(@Param("user1") User user1, @Param("user2") User user2);

    @Query("SELECT c FROM Conversation c WHERE (c.user1 = :user OR c.user2 = :user) ORDER BY (SELECT MAX(m.createdAt) FROM Message m WHERE m.conversation = c) DESC")
    List<Conversation> findByUserSortedByLastMessage(@Param("user") User user, Pageable pageable);

    // New method to find conversations by user
    @Query("SELECT c FROM Conversation c WHERE c.user1 = :user OR c.user2 = :user")
    List<Conversation> findByUser(User user);
}