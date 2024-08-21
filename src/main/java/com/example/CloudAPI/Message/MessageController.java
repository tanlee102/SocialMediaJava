package com.example.CloudAPI.Message;

import com.example.CloudAPI.Conversation.Conversation;
import com.example.CloudAPI.Conversation.ConversationRepository;
import com.example.CloudAPI.Users.model.User;
import com.example.CloudAPI.Users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/messages")
@CrossOrigin(origins = "*")
public class MessageController {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConversationRepository conversationRepository;

    @GetMapping
    public ResponseEntity<List<MessageDTO>> gettingMessages(
            @RequestParam("conversationId") Long conversationId,
            @RequestParam(value = "lastMessageId", required = false) Long lastMessageId,
            @RequestParam("size") int size) {

        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid conversation ID"));

        Pageable pageable = PageRequest.of(0, size);
        List<Message> messages;

        if (lastMessageId != null) {
            messages = messageRepository.findByConversationAndIdLessThanOrderByIdDesc(conversation, lastMessageId, pageable);
        } else {
            messages = messageRepository.findByConversationOrderByIdDesc(conversation, pageable);
        }

        // Update the readAt timestamp to the current time
        conversation.setReadAt(LocalDateTime.now());
        conversationRepository.save(conversation);

        List<MessageDTO> messageDTOs = messages.stream()
                .filter(message -> message != null) // Filter out null messages
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(messageDTOs);
    }

    private MessageDTO convertToDTO(Message message) {

        if (message == null) {
            // Handle null message case
            return null;
        }

        MessageDTO dto = new MessageDTO();
        dto.setId(message.getId());
        dto.setContent(message.getContent());
        dto.setConversationId(message.getConversation().getId());
        dto.setSenderId(message.getSender().getId());
        dto.setCreatedAt(message.getCreatedAt());
        return dto;
    }

}