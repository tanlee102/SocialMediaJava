package com.example.CloudAPI.Notifications;

import com.example.CloudAPI.Users.model.User;
import com.example.CloudAPI.Users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Notification> getNotificationsForUser(Long userId) {
        return notificationRepository.findByUserIdOrderByIdDesc(userId);
    }

    public Notification createNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

    public List<NotificationSummary> getGroupedNotificationsForUser(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        List<Notification> notifications = notificationRepository.findByUserId(userId, pageable);
        markNotificationsAsRead(notifications);
        return groupNotificationsByTypeAndEntityId(notifications);
    }

    public void updateNotificationReadStatus(Long notificationId, boolean readStatus) {
        Optional<Notification> optionalNotification = notificationRepository.findById(notificationId);
        if (optionalNotification.isPresent()) {
            Notification notification = optionalNotification.get();
            notification.setReadStatus(readStatus);
            notificationRepository.save(notification);
        }
    }

    public long countUnreadNotifications(Long userId) {
        return notificationRepository.countByUserIdAndReadStatusFalse(userId);
    }

    public void markNotificationsAsRead(List<Notification> notifications) {
        notifications.forEach(notification -> notification.setReadStatus(true));
        notificationRepository.saveAll(notifications);
    }

    private List<NotificationSummary> groupNotificationsByTypeAndEntityId(List<Notification> notifications) {
        // Group notifications by type and entity ID
        Map<String, Map<Long, List<Notification>>> groupedNotifications = notifications.stream()
                .collect(Collectors.groupingBy(
                        Notification::getType,
                        LinkedHashMap::new,
                        Collectors.groupingBy(
                                Notification::getEntityId,
                                LinkedHashMap::new,
                                Collectors.toList()
                        )
                ));

        // Create a list of NotificationSummary objects, sorting by the most recent creation time
        List<NotificationSummary> notificationSummaries = groupedNotifications.entrySet().stream()
                .flatMap(typeEntry -> typeEntry.getValue().entrySet().stream()
                        .map(entityEntry -> {
                            List<Notification> notificationList = entityEntry.getValue();
                            LocalDateTime latestCreatedAt = notificationList.stream()
                                    .map(Notification::getCreatedAt)
                                    .max(LocalDateTime::compareTo)
                                    .orElse(null);

                            List<NotificationSummary.ActorDetails> actorDetails = notificationList.stream()
                                    .map(notification -> {
                                        User actor = userRepository.findById(notification.getActorId()).orElse(null);
                                        return new NotificationSummary.ActorDetails(
                                                notification.getActorId(),
                                                actor != null ? actor.getUsername() : null,
                                                actor != null && actor.getUserProfile() != null ? actor.getUserProfile().getProfileImageUrl() : null
                                        );
                                    })
                                    .distinct()
                                    .collect(Collectors.toList());

                            return new NotificationSummary(
                                    typeEntry.getKey(),
                                    entityEntry.getKey(),
                                    actorDetails,
                                    latestCreatedAt
                            );
                        }))
                .sorted(Comparator.comparing(NotificationSummary::getLatestCreatedAt).reversed())
                .collect(Collectors.toList());

        return notificationSummaries;
    }

    public void createNotification(User user, Long actorId, Long entityId, String type) {
        if (!user.getId().equals(actorId)) {
            Notification notification = new Notification();
            notification.setUser(user);
            notification.setActorId(actorId);
            notification.setEntityId(entityId);
            notification.setType(type);
            notificationRepository.save(notification);
        }
    }
}