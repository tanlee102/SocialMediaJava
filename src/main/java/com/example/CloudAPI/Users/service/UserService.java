package com.example.CloudAPI.Users.service;

import com.example.CloudAPI.Block.BlockId;
import com.example.CloudAPI.Block.BlockRepository;
import com.example.CloudAPI.Followers.FollowerId;
import com.example.CloudAPI.Followers.FollowerRepository;
import com.example.CloudAPI.Posts.PostRepository;
import com.example.CloudAPI.Role.Role;
import com.example.CloudAPI.UserProfiles.UserProfile;
import com.example.CloudAPI.UserProfiles.UserProfileRepository;
import com.example.CloudAPI.Users.model.CustomUserDetails;
import com.example.CloudAPI.Users.model.UserProfileDto;
import com.example.CloudAPI.Users.repository.UserRepository;
import com.example.CloudAPI.Users.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final FollowerRepository followerRepository;
    private final PostRepository postRepository;
    private final BlockRepository blockRepository;

    public UserService(UserRepository userRepository, UserProfileRepository userProfileRepository, FollowerRepository followerRepository, PostRepository postRepository, BlockRepository blockRepository) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.followerRepository = followerRepository;
        this.postRepository = postRepository;
        this.blockRepository = blockRepository;
    }


    public List<User> getUsersByRoleId(Long roleId) {
        return userRepository.findByRoleId(roleId);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean isEmailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public boolean isUsernameExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public boolean changePassword(String password, String email){
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setPassword(password);
            userRepository.save(user);
            return true;
        } else {
            return false;
        }
    }

    public User updateUsername(String newUsername, Long id){
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setUsername(newUsername);
            userRepository.save(user);
            return user;
        } else {
            return null;
        }
    }

    @Cacheable(value = "userAuthCache", key = "#email", unless = "#result == null")
    public Optional<CustomUserDetails> findByCustomUserDetail(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            CustomUserDetails customUserDetails = new CustomUserDetails();
            customUserDetails.setId(user.getId());
            customUserDetails.setUsername(user.getUsername());
            customUserDetails.setEmail(user.getEmail());
            customUserDetails.setPassword(user.getPassword());
            customUserDetails.setRole(user.getRole().getName());
            customUserDetails.setBanned(user.isBanned());
            return Optional.of(customUserDetails);
        } else {
            return Optional.empty();
        }
    }

    public boolean updateUrlThumbnail(Long userId, String profileImageUrl) {
        UserProfile userProfile = userProfileRepository.findByUserId(userId);
        userProfile.setProfileImageUrl(profileImageUrl);
        userProfileRepository.save(userProfile);
        return true;
    }

    public boolean updateUserProfileInfo(Long userId, UserProfile profile) {
        UserProfile userProfile = userProfileRepository.findByUserId(userId);
        userProfile.setBio(profile.getBio());
        userProfileRepository.save(userProfile);
        return true;
    }

    public long getNumberOfFollowers(Long userId) {
        return followerRepository.countByFollowerId(userId);
    }

    public long getNumberOfFollowing(Long userId) {
        return followerRepository.countByUserId(userId);
    }

    public long getNumberOfPosts(Long userId) {
        return postRepository.countByUserId(userId);
    }

    public UserProfileDto getUserProfileByUsername(String username, Long userId) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return convertToUserProfileDto(user, userId);
        } else {
            return null;
        }
    }

    public UserProfileDto getUserProfileById(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return convertToUserProfileDto(user, null);
        } else {
            return null;
        }
    }

    public Page<UserProfileDto> getTopUsersByFollowers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> usersPage = userRepository.findAllByOrderByFollowersDesc(pageable);
        return usersPage.map(user -> convertToUserProfileDto(user, null));
    }

    public Page<UserProfileDto> getLatestUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> usersPage = userRepository.findAllByOrderByCreatedAtDesc(pageable);
        return usersPage.map(user -> convertToUserProfileDto(user, null));
    }

    public Page<UserProfileDto> getLatestBannedUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> usersPage = userRepository.findByBannedTrueOrderByCreatedAtDesc(pageable);
        return usersPage.map(user -> convertToUserProfileDto(user, null));
    }

    private UserProfileDto convertToUserProfileDto(User user, Long userId) {
        UserProfileDto userProfileDto = new UserProfileDto();
        userProfileDto.setUsername(user.getUsername());
        userProfileDto.setId(user.getId());
        userProfileDto.setEmail(user.getEmail());
        userProfileDto.setVerified(user.isVerified());
        userProfileDto.setBanned(user.isBanned());
        if (user.getUserProfile() != null) {
            userProfileDto.setProfileImageUrl(user.getUserProfile().getProfileImageUrl());
            userProfileDto.setBio(user.getUserProfile().getBio());
        }
        userProfileDto.setFollowersCount(getNumberOfFollowers(user.getId()));
        userProfileDto.setFollowingCount(getNumberOfFollowing(user.getId()));
        userProfileDto.setPostsCount(getNumberOfPosts(user.getId()));

        if (userId != null) {
            FollowerId followerIdObj = new FollowerId(userId, user.getId());
            userProfileDto.setIsFollowing(followerRepository.existsById(followerIdObj));

            BlockId blockId = new BlockId(userId, user.getId());
            userProfileDto.setBlocked(blockRepository.existsById(blockId));

            BlockId beBlockId = new BlockId(user.getId(), userId);
            userProfileDto.setBeBlocked(blockRepository.existsById(beBlockId));
        } else {
            userProfileDto.setIsFollowing(false);
            userProfileDto.setBlocked(false);
        }
        return userProfileDto;
    }

    public boolean toggleUserBan(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setBanned(!user.isBanned());
            userRepository.save(user);
            return true;
        } else {
            return false;
        }
    }

    public boolean changeUserRole(Long userId, Long roleId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent() && userOptional.get().getRole().getId() <= 2) {
            User user = userOptional.get();
            Role role = new Role();
            role.setId(roleId);  // Assuming role IDs are 1 for USER and 2 for ADMIN
            user.setRole(role);
            userRepository.save(user);
            return true;
        } else {
            return false;
        }
    }

    public List<User> getUsersByRoleIdGreaterThan(Long roleId) {
        return userRepository.findUsersByRoleIdGreaterThan(roleId);
    }
}
