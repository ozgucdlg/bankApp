package com.restApi.bankApp.business.abstracts;

import com.restApi.bankApp.entities.Notification;
import java.util.List;

public interface NotificationService {
    void sendNotification(String recipient, String subject, String content);
    List<Notification> getAllNotifications();
    List<Notification> getNotificationsByRecipient(String recipient);
    void markNotificationAsSent(Long notificationId);
    void markNotificationAsFailed(Long notificationId, String errorMessage);
} 