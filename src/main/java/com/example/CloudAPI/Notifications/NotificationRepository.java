package com.example.CloudAPI.Notifications;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserId(Long userId, Pageable pageable);
    List<Notification> findByUserIdOrderByIdDesc(Long userId);
    long countByUserIdAndReadStatusFalse(Long userId);
}