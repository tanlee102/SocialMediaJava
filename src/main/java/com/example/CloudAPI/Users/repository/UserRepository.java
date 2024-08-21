package com.example.CloudAPI.Users.repository;

import com.example.CloudAPI.Users.model.User;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    Page<User> findAllByOrderByFollowersDesc(Pageable pageable);
    Page<User> findAllByOrderByCreatedAtDesc(Pageable pageable);

    List<User> findByRoleId(Long roleId);

    Page<User> findByBannedTrueOrderByCreatedAtDesc(Pageable pageable);

    // Method to find users with role ID greater than a specified value
    @Query("SELECT u FROM User u WHERE u.role.id > :roleId")
    List<User> findUsersByRoleIdGreaterThan(Long roleId);
}