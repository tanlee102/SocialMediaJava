package com.example.CloudAPI.Block;

import com.example.CloudAPI.Users.model.User;
import com.example.CloudAPI.Users.model.UserDTO;
import com.example.CloudAPI.Users.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/blocks")
@CrossOrigin(origins = "*")
public class BlockController {

    @Autowired
    private BlockRepository blockRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/block")
    public ResponseEntity<?> blockUser(HttpServletRequest request, @RequestParam Long blockedId) {

        Long userId = Long.parseLong(request.getAttribute("userId").toString());

        Optional<User> blockerOpt = userRepository.findById(userId);
        Optional<User> blockedOpt = userRepository.findById(blockedId);

        if (blockerOpt.isPresent() && blockedOpt.isPresent()) {
            User blocker = blockerOpt.get();
            User blocked = blockedOpt.get();

            BlockId blockId = new BlockId(userId, blockedId);
            Block block = new Block(blockId, blocker, blocked);
            blockRepository.save(block);

            return ResponseEntity.ok("User blocked successfully");
        } else {
            return ResponseEntity.status(404).body("User not found");
        }
    }

    @DeleteMapping("/unblock")
    public ResponseEntity<?> unblockUser(HttpServletRequest request, @RequestParam Long blockedId) {
        Long userId = Long.parseLong(request.getAttribute("userId").toString());

        BlockId blockId = new BlockId(userId, blockedId);
        Optional<Block> blockOpt = blockRepository.findById(blockId);

        if (blockOpt.isPresent()) {
            blockRepository.deleteById(blockId);
            return ResponseEntity.ok("User unblocked successfully");
        } else {
            return ResponseEntity.status(404).body("Block relation not found");
        }
    }


    @PostMapping("/blocked-users")
    public ResponseEntity<List<UserDTO>> getBlockedUsers(HttpServletRequest request) {
        Long userId = Long.parseLong(request.getAttribute("userId").toString());

        List<User> blockedUsers = blockRepository.findBlockedUsersByBlockerId(userId);

        List<UserDTO> blockedUsersDTOs = blockedUsers.stream()
                .map(user -> new UserDTO(user.getId(), user.getUsername(), user.getUserProfile().getProfileImageUrl()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(blockedUsersDTOs);
    }
}
