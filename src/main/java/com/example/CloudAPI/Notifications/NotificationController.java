package com.example.CloudAPI.Notifications;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/notification")
@CrossOrigin(origins = "*")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;

    @GetMapping("/{userId}")
    public List<Notification> getUserNotifications(@PathVariable Long userId) {
        notificationService.markNotificationsAsRead(notificationService.getNotificationsForUser(userId));
        return notificationService.getNotificationsForUser(userId);
    }

    @PostMapping
    public Notification createNotification(@RequestBody Notification notification) {
        return notificationService.createNotification(notification);
    }

    @GetMapping("/users/{userId}/notifications")
    public List<NotificationSummary> getGroupedNotifications(
            @PathVariable Long userId,
            @RequestParam int page,
            @RequestParam int size) {
        return notificationService.getGroupedNotificationsForUser(userId, page, size);
    }

    @GetMapping("/users/{userId}/unread-count")
    public long countUnreadNotifications(@PathVariable Long userId) {
        return notificationService.countUnreadNotifications(userId);
    }
}