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
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        if (!username.equals(recipient)) {
            return ResponseEntity.status(403).body(null);
        }
        return ResponseEntity.ok(notificationService.getNotificationsByRecipient(recipient));
    }
} 