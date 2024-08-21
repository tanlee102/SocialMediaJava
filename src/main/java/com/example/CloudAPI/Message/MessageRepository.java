package com.example.CloudAPI.Message;

import com.example.CloudAPI.Conversation.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@EnableJpaRepositories
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByConversation(Conversation conversation);
    Message findFirstByConversationOrderByCreatedAtDesc(Conversation conversation);
    Page<Message> findByConversation(Conversation conversation, Pageable pageable);

    List<Message> findByConversationAndIdLessThanOrderByIdDesc(Conversation conversation, Long id, Pageable pageable);
    List<Message> findByConversationOrderByIdDesc(Conversation conversation, Pageable pageable);
}