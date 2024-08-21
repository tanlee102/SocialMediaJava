package com.example.CloudAPI.Conversation;

import com.example.CloudAPI.Block.BlockId;
import com.example.CloudAPI.Block.BlockRepository;
import com.example.CloudAPI.Message.Message;
import com.example.CloudAPI.Message.MessageDTO;
import com.example.CloudAPI.Message.MessageReceiveDTO;
import com.example.CloudAPI.Message.MessageRepository;
import com.example.CloudAPI.UserProfiles.UserProfileDTO;
import com.example.CloudAPI.Users.model.User;
import com.example.CloudAPI.Users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@CrossOrigin(origins = "*")
public class ConversationController {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private BlockRepository blockRepository;

    @MessageMapping("/chat")
    public void sendMessage(@Payload MessageReceiveDTO messageReceiveDTO) {
        User sender = userRepository.findById(messageReceiveDTO.getSenderId()).orElseThrow(() -> new IllegalArgumentException("Invalid sender ID"));
        User receiver = userRepository.findById(messageReceiveDTO.getReceiverId()).orElseThrow(() -> new IllegalArgumentException("Invalid receiver ID"));

        // Check if the receiver has blocked the user
        if (isUserBlocked(receiver.getId(), sender.getId())) {
            throw new RuntimeException("You are blocked from on this user");
        }

        if (isUserBlocked(sender.getId() , receiver.getId())) {
            throw new RuntimeException("You are blocking this user");
        }

        Conversation conversation = conversationRepository.findByUsers(sender, receiver);

        if (conversation == null) {
            conversation = new Conversation();
            conversation.setUser1(sender);
            conversation.setUser2(receiver);
            conversationRepository.save(conversation);
        }

        Message message = new Message();
        message.setContent(messageReceiveDTO.getContent());
        message.setSender(sender);
        message.setConversation(conversation);

        messageRepository.save(message);

        MessageDTO messageDTO = convertToDTO(message);

        messagingTemplate.convertAndSendToUser(receiver.getId().toString(), "/queue/messages", messageDTO);
    }

    @GetMapping("/api/conversation")
    @ResponseBody
    public Conversation findOrCreateConversation(@RequestParam("userId1") Long userId1, @RequestParam("userId2") Long userId2) {
        User user1 = userRepository.findById(userId1).orElseThrow(() -> new IllegalArgumentException("Invalid user ID 1"));
        User user2 = userRepository.findById(userId2).orElseThrow(() -> new IllegalArgumentException("Invalid user ID 2"));

        Conversation conversation = conversationRepository.findByUsers(user1, user2);

        if (conversation == null) {
            conversation = new Conversation();
            conversation.setUser1(user1);
            conversation.setUser2(user2);
            return conversationRepository.save(conversation);
        }

        return conversation;
    }

    @GetMapping("/api/conversations")
    @ResponseBody
    public List<ConversationDTO> getConversations(@RequestParam("userId") Long userId,
                                                  @RequestParam(value = "page", defaultValue = "0") int page,
                                                  @RequestParam(value = "size", defaultValue = "10") int size) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));
        Pageable pageable = PageRequest.of(page, size);
        List<Conversation> conversations = conversationRepository.findByUserSortedByLastMessage(user, pageable);
        return conversations.stream().map(conversation -> convertToConversationDTO(conversation, user)).collect(Collectors.toList());
    }

    private ConversationDTO convertToConversationDTO(Conversation conversation, User requestingUser) {
        ConversationDTO dto = new ConversationDTO();
        dto.setId(conversation.getId());
        dto.setCreatedAt(conversation.getCreatedAt());

        // Determine the other user in the conversation
        User otherUser = conversation.getUser1().equals(requestingUser) ? conversation.getUser2() : conversation.getUser1();
        dto.setOtherUser(convertToUserProfileDTO(otherUser));

        // Get the last message
        Message lastMessage = messageRepository.findFirstByConversationOrderByCreatedAtDesc(conversation);
        dto.setLastMessage(convertToDTO(lastMessage));

        // Check if there are any unread messages after the readAt time
        LocalDateTime readAt = conversation.getReadAt();
        boolean readAllMessages = conversation.getMessages().stream()
                .noneMatch(message -> message.getCreatedAt().isAfter(readAt));

        dto.setReadAllMessages(readAllMessages);

        // Check if the conversation is blocked
        boolean isBlocked = isUserBlocked(requestingUser.getId(), otherUser.getId()) || isUserBlocked(otherUser.getId(), requestingUser.getId());
        dto.setBlocked(isBlocked);

        return dto;
    }

    private UserProfileDTO convertToUserProfileDTO(User user) {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setUserId(user.getId());
        dto.setUsername(user.getUsername());
        if (user.getUserProfile() != null) {
            dto.setProfileImageUrl(user.getUserProfile().getProfileImageUrl());
            dto.setBio(user.getUserProfile().getBio());
        }
        return dto;
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



    @GetMapping("/api/conversations/unread/count")
    @ResponseBody
    public ResponseEntity<Long> countUnreadConversations(@RequestParam("userId") Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));

        // Fetch conversations for the user
        List<Conversation> conversations = conversationRepository.findByUser(user);

        // Count conversations with unread messages
        long unreadCount = conversations.stream()
                .filter(conversation -> hasUnreadMessages(conversation))
                .count();

        return ResponseEntity.ok(unreadCount);
    }

    private boolean hasUnreadMessages(Conversation conversation) {
        LocalDateTime readAt = conversation.getReadAt();

        // Check if any message in the conversation is created after the readAt time
        return conversation.getMessages().stream()
                .anyMatch(message -> message.getCreatedAt().isAfter(readAt));
    }


    private boolean isUserBlocked(Long blockerId, Long blockedId) {
        return blockRepository.existsById(new BlockId(blockerId, blockedId));
    }
}