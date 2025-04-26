package com.restApi.bankApp.business.concretes;

import com.restApi.bankApp.business.abstracts.NotificationService;
import com.restApi.bankApp.entities.Notification;
import com.restApi.bankApp.dataAccess.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationManager implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired(required = false)
    private JavaMailSender emailSender;

    @Override
    public void sendNotification(String recipient, String subject, String content) {
        Notification notification = new Notification();
        notification.setRecipient(recipient);
        notification.setSubject(subject);
        notification.setContent(content);
        notification.setMessage(content);
        notification.setSentAt(LocalDateTime.now());
        notification.setStatus("PENDING");

        // First save the notification
        notification = notificationRepository.save(notification);

        try {
            // Try to send email if email sender is configured
            if (emailSender != null) {
                notification.setNotificationType("EMAIL");
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(recipient);
                message.setSubject(subject);
                message.setText(content);
                emailSender.send(message);
                markNotificationAsSent(notification.getId());
            } else {
                // Fallback to console notification
                notification.setNotificationType("CONSOLE");
                System.out.println("=== NOTIFICATION ===");
                System.out.println("To: " + recipient);
                System.out.println("Subject: " + subject);
                System.out.println("Content: " + content);
                System.out.println("==================");
                markNotificationAsSent(notification.getId());
            }
        } catch (Exception e) {
            markNotificationAsFailed(notification.getId(), e.getMessage());
            // Log the error but don't throw it to prevent disrupting the main flow
            System.err.println("Failed to send notification: " + e.getMessage());
        }
    }

    @Override
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    @Override
    public List<Notification> getNotificationsByRecipient(String recipient) {
        return notificationRepository.findByRecipient(recipient);
    }

    @Override
    public void markNotificationAsSent(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setStatus("SENT");
            notification.setSentAt(LocalDateTime.now());
            notificationRepository.save(notification);
        });
    }

    @Override
    public void markNotificationAsFailed(Long notificationId, String errorMessage) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setStatus("FAILED");
            String updatedContent = notification.getContent() + "\nError: " + errorMessage;
            notification.setContent(updatedContent);
            notification.setMessage(updatedContent);
            notificationRepository.save(notification);
        });
    }

    @Override
    public void markNotificationAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setRead(true);
            notificationRepository.save(notification);
        });
    }

    @Override
    public void markAllNotificationsAsRead(String recipient) {
        List<Notification> notifications = notificationRepository.findByRecipient(recipient);
        for (Notification notification : notifications) {
            notification.setRead(true);
            notificationRepository.save(notification);
        }
    }

    @Override
    public List<Notification> getUnreadNotifications(String recipient) {
        List<Notification> notifications = notificationRepository.findByRecipient(recipient);
        return notifications.stream()
                .filter(notification -> notification.getRead() == null || !notification.getRead())
                .collect(Collectors.toList());
    }

    @Override
    public List<Notification> getNotificationsByRecipientAndStatus(String recipient, String status) {
        return notificationRepository.findByRecipientAndStatus(recipient, status);
    }

    @Override
    public List<Notification> getNotificationsByStatus(String status) {
        return notificationRepository.findByStatus(status);
    }

    @Override
    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }
} 