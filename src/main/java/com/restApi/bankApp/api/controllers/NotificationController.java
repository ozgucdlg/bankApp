package com.restApi.bankApp.api.controllers;

import com.restApi.bankApp.business.abstracts.NotificationService;
import com.restApi.bankApp.entities.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
        try {
            notificationService.sendNotification(recipient, subject, content);
            return ResponseEntity.ok("Notification sent successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to send notification: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Notification>> getAllNotifications() {
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }

    @GetMapping("/recipient/{recipient}")
    public ResponseEntity<List<Notification>> getNotificationsByRecipient(@PathVariable String recipient) {
        return ResponseEntity.ok(notificationService.getNotificationsByRecipient(recipient));
    }
} 