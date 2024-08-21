package com.example.CloudAPI.Users.controller;

import com.example.CloudAPI.UserProfiles.UserProfile;
import com.example.CloudAPI.UserProfiles.UserProfileDTO;
import com.example.CloudAPI.Users.model.AuthenticationResponse;
import com.example.CloudAPI.Users.model.RequestUser;
import com.example.CloudAPI.Users.model.UserProfileDto;
import com.example.CloudAPI.Users.service.AuthenticationService;
import com.example.CloudAPI.Users.service.UserService;
import com.example.CloudAPI.Users.model.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationService authenticationService;

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        User myUser = user.get();
        User reUser = new User();
        reUser.setEmail(myUser.getEmail());
        reUser.setUsername(myUser.getUsername());
        reUser.setVerified(myUser.isVerified());

        UserProfile userProfile = new UserProfile();
        userProfile.setProfileImageUrl(myUser.getUserProfile().getProfileImageUrl());
        userProfile.setBio((myUser.getUserProfile().getBio()));

        reUser.setUserProfile(userProfile);

        return ResponseEntity.ok(reUser);
    }

    @GetMapping("/{username}/exists")
    public boolean checkUsernameExists(@PathVariable String username) {
        return userService.isUsernameExists(username);
    }

    @PostMapping("/thumbnail/update")
    public boolean updateUrlThumbnail(HttpServletRequest request, @RequestBody UserProfile userProfile) {
        Long userId = Long.parseLong(request.getAttribute("userId").toString());
        return userService.updateUrlThumbnail(userId,userProfile.getProfileImageUrl());
    }

    @PostMapping("/profile/update")
    public ResponseEntity<String> updateUserProfileInfo(HttpServletRequest request, @RequestBody UserProfile userProfile) {
        Long userId = Long.parseLong(request.getAttribute("userId").toString());
        userService.updateUserProfileInfo(userId,userProfile);
        return ResponseEntity.ok("Profile updated");
    }

    @PostMapping("/update/username")
    public ResponseEntity<AuthenticationResponse> updateUsername(@RequestBody RequestUser requestUser, HttpServletRequest request) {

        if(userService.isUsernameExists(requestUser.getUsername())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }else{
            Long userId = (Long) request.getAttribute("userId");
            try{
                User userDetails = userService.updateUsername(requestUser.getUsername(), userId);
                return ResponseEntity.ok(authenticationService.generateDirectToken(userDetails));
            }catch(Exception e){
                return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
            }
        }
    }

    @GetMapping("/profile/{username}")
    public ResponseEntity<UserProfileDto> getUserProfile(@RequestParam(required = false) Long userId, @PathVariable String username) {
        UserProfileDto userProfileDto = userService.getUserProfileByUsername(username, userId);
        if (userProfileDto != null) {
            return ResponseEntity.ok(userProfileDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileDto> getUserProfilePrivate(HttpServletRequest request) {
        Long userId = Long.parseLong(request.getAttribute("userId").toString());
        UserProfileDto userProfileDto = userService.getUserProfileById(userId);
        if (userProfileDto != null) {
            return ResponseEntity.ok(userProfileDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/top")
    public ResponseEntity<Page<UserProfileDto>> getTopUsersByFollowers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<UserProfileDto> usersPage = userService.getTopUsersByFollowers(page, size);
        return ResponseEntity.ok(usersPage);
    }

    @GetMapping("/latest")
    public ResponseEntity<Page<UserProfileDto>> getLatestUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<UserProfileDto> usersPage = userService.getLatestUsers(page, size);
        return ResponseEntity.ok(usersPage);
    }

    @GetMapping("/latest-banned")
    public ResponseEntity<Page<UserProfileDto>> getLatestBannedUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<UserProfileDto> usersPage = userService.getLatestBannedUsers(page, size);
        return ResponseEntity.ok(usersPage);
    }

    @PostMapping("/toggle-ban/{id}")
    public ResponseEntity<String> toggleUserBan(@PathVariable Long id) {
        boolean result = userService.toggleUserBan(id);
        if (result) {
            return ResponseEntity.ok("User ban status toggled successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }

    @GetMapping("/role/2")
    public ResponseEntity<List<UserProfileDTO>> getUsersByRoleId() {
        List<User> users = userService.getUsersByRoleId(2L);
        List<UserProfileDTO> userProfiles = users.stream().map(user -> {
            UserProfileDTO userProfileDto = new UserProfileDTO();
            userProfileDto.setUserId(user.getId());
            userProfileDto.setUsername(user.getUsername());
            userProfileDto.setProfileImageUrl(user.getUserProfile().getProfileImageUrl());
            userProfileDto.setBio(user.getUserProfile().getBio());
            return userProfileDto;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(userProfiles);
    }


    @PostMapping("/role/admin/{id}")
    public ResponseEntity<String> promoteToAdmin(@PathVariable Long id) {
        boolean result = userService.changeUserRole(id, 2L);  // Assuming role ID 2 is for ADMIN
        if (result) {
            return ResponseEntity.ok("User promoted to ADMIN successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }

    @GetMapping("/role/greater-than/{roleId}")
    public ResponseEntity<List<UserProfileDTO>> getUsersWithRoleGreaterThan(@PathVariable Long roleId) {
        List<User> users = userService.getUsersByRoleIdGreaterThan(roleId);
        List<UserProfileDTO> userProfiles = users.stream().map(user -> {
            UserProfileDTO userProfileDto = new UserProfileDTO();
            userProfileDto.setUserId(user.getId());
            userProfileDto.setUsername(user.getUsername());
            userProfileDto.setProfileImageUrl(user.getUserProfile().getProfileImageUrl());
            userProfileDto.setBio(user.getUserProfile().getBio());
            userProfileDto.setRole(user.getRole());
            return userProfileDto;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(userProfiles);
    }

    @PostMapping("/role/user/{id}")
    public ResponseEntity<String> demoteToUser(@PathVariable Long id) {
        boolean result = userService.changeUserRole(id, 1L);  // Assuming role ID 1 is for USER
        if (result) {
            return ResponseEntity.ok("User demoted to USER successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }
}