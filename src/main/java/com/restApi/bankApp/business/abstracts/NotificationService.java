package com.restApi.bankApp.business.abstracts;

import com.restApi.bankApp.entities.Notification;
import java.util.List;

public interface NotificationService {
    void sendNotification(String recipient, String subject, String content);
    List<Notification> getAllNotifications();
    List<Notification> getNotificationsByRecipient(String recipient);
    List<Notification> getNotificationsByRecipientAndStatus(String recipient, String status);
    List<Notification> getNotificationsByStatus(String status);
    void markNotificationAsSent(Long notificationId);
    void markNotificationAsFailed(Long notificationId, String errorMessage);
    void markNotificationAsRead(Long notificationId);
    void markAllNotificationsAsRead(String recipient);
    List<Notification> getUnreadNotifications(String recipient);
    void deleteNotification(Long notificationId);
} 