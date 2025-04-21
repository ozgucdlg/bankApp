package com.restApi.bankApp.controllers;

import com.restApi.bankApp.business.abstracts.NotificationService;
import com.restApi.bankApp.entities.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/send")
    public ResponseEntity<?> sendNotification(
            @RequestParam String recipient,
            @RequestParam String subject,
            @RequestParam String content) {
        System.out.println("Received notification request - Recipient: " + recipient); // Debug log
        try {
            notificationService.sendNotification(recipient, subject, content);
            System.out.println("Notification sent successfully"); // Debug log
            return ResponseEntity.ok("Notification sent successfully");
        } catch (Exception e) {
            System.out.println("Failed to send notification: " + e.getMessage()); // Debug log
            return ResponseEntity.badRequest().body("Failed to send notification: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Notification>> getAllNotifications() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        if (!username.equals("ADMIN")) {
            return ResponseEntity.status(403).body(null);
        }
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }

    @GetMapping("/recipient/{recipient}")
    public ResponseEntity<List<Notification>> getNotificationsByRecipient(@PathVariable String recipient) {
        return ResponseEntity.ok(notificationService.getNotificationsByRecipient(recipient));
    }
    
    @GetMapping("/unread/{recipient}")
    public ResponseEntity<List<Notification>> getUnreadNotifications(@PathVariable String recipient) {
        return ResponseEntity.ok(notificationService.getUnreadNotifications(recipient));
    }
    
    @PutMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        List<Notification> notifications = notificationService.getAllNotifications();
        Notification notification = notifications.stream()
            .filter(n -> n.getId().equals(id))
            .findFirst()
            .orElse(null);
            
        if (notification == null) {
            return ResponseEntity.notFound().build();
        }
        
        if (!notification.getRecipient().equals(username) && !username.equals("ADMIN")) {
            return ResponseEntity.status(403).body("Access denied");
        }
        
        notificationService.markNotificationAsRead(id);
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/read-all/{recipient}")
    public ResponseEntity<?> markAllAsRead(@PathVariable String recipient) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        if (!username.equals(recipient) && !username.equals("ADMIN")) {
            return ResponseEntity.status(403).body("Access denied");
        }
        
        notificationService.markAllNotificationsAsRead(recipient);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/all")
    public ResponseEntity<List<Notification>> getAllNotificationsForRecipient(
            @RequestParam String recipient) {
        System.out.println("Getting all notifications for recipient: " + recipient);
        List<Notification> notifications = notificationService.getNotificationsByRecipient(recipient);
        System.out.println("Found " + notifications.size() + " notifications");
        return ResponseEntity.ok(notifications);
    }

    @PutMapping("/read-all")
    public ResponseEntity<?> markAllAsReadWithParam(@RequestParam String recipient) {
        System.out.println("Marking all notifications as read for: " + recipient);
        notificationService.markAllNotificationsAsRead(recipient);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/get-by-username")
    public ResponseEntity<List<Notification>> getNotificationsByUsername(
            @RequestParam String username) {
        System.out.println("Direct endpoint - Getting notifications for user: " + username);
        try {
            List<Notification> notifications = notificationService.getNotificationsByRecipient(username);
            System.out.println("Found " + notifications.size() + " notifications for user: " + username);
            for (Notification notification : notifications) {
                System.out.println(" - Notification: " + notification.getSubject() + " [read: " + notification.getRead() + "]");
            }
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            System.out.println("ERROR retrieving notifications: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/create-test-notification/{username}")
    public ResponseEntity<?> createTestNotification(@PathVariable String username) {
        System.out.println("Creating test notification for: " + username);
        try {
            notificationService.sendNotification(
                username,
                "Test Notification",
                "This is a test notification created manually to verify the notification system."
            );
            System.out.println("Test notification created successfully");
            
            // Verify it was created
            List<Notification> notifications = notificationService.getNotificationsByRecipient(username);
            System.out.println("Found " + notifications.size() + " notifications for user " + username);
            for (Notification n : notifications) {
                System.out.println(" - " + n.getSubject() + " [" + n.getStatus() + "] [read: " + n.getRead() + "]");
            }
            
            return ResponseEntity.ok("Test notification created successfully");
        } catch (Exception e) {
            System.out.println("Failed to create test notification: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to create test notification: " + e.getMessage());
        }
    }

    @GetMapping("/sent/{username}")
    public ResponseEntity<List<Notification>> getSentNotifications(@PathVariable String username) {
        System.out.println("Getting SENT notifications for user: " + username);
        List<Notification> notifications = notificationService.getNotificationsByRecipientAndStatus(username, "SENT");
        System.out.println("Found " + notifications.size() + " SENT notifications for user " + username);
        for (Notification n : notifications) {
            System.out.println(" - " + n.getSubject() + " [" + n.getStatus() + "] [read: " + n.getRead() + "]");
        }
        return ResponseEntity.ok(notifications);
    }
    
    @GetMapping("/fix-null-read-values")
    public ResponseEntity<?> fixNullReadValues() {
        System.out.println("Fixing any null 'read' values in notifications...");
        try {
            List<Notification> allNotifications = notificationService.getAllNotifications();
            int fixedCount = 0;
            
            for (Notification notification : allNotifications) {
                // Check if getRead would handle the null case
                if (notification.getRead() == null) {
                    notification.setRead(false);
                    fixedCount++;
                }
            }
            
            if (fixedCount > 0) {
                System.out.println("Fixed " + fixedCount + " notifications with null 'read' values");
                return ResponseEntity.ok("Fixed " + fixedCount + " notifications with null 'read' values");
            } else {
                System.out.println("No notifications with null 'read' values found");
                return ResponseEntity.ok("No notifications with null 'read' values found");
            }
        } catch (Exception e) {
            System.out.println("Error fixing null 'read' values: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error fixing null 'read' values: " + e.getMessage());
        }
    }
} 