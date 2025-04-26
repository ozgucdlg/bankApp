package com.restApi.bankApp.controllers;

import com.restApi.bankApp.business.abstracts.NotificationService;
import com.restApi.bankApp.dto.NotificationDto;
import com.restApi.bankApp.dto.NotificationRequest;
import com.restApi.bankApp.entities.Notification;
import com.restApi.bankApp.exceptions.ResourceNotFoundException;
import com.restApi.bankApp.mappers.NotificationMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationMapper notificationMapper;

    @Autowired
    public NotificationController(NotificationService notificationService, NotificationMapper notificationMapper) {
        this.notificationService = notificationService;
        this.notificationMapper = notificationMapper;
    }

    /**
     * Send a new notification
     */
    @PostMapping("/send")
    public ResponseEntity<String> sendNotification(@Valid @RequestBody NotificationRequest request) {
        notificationService.sendNotification(request.getRecipient(), request.getSubject(), request.getContent());
        return ResponseEntity.status(HttpStatus.CREATED).body("Notification sent successfully");
    }

    /**
     * Get all notifications (admin only)
     */
    @GetMapping
    public ResponseEntity<List<NotificationDto>> getAllNotifications() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        if (!username.equals("ADMIN")) {
            throw new AccessDeniedException("Only admins can view all notifications");
        }
        
        List<Notification> notifications = notificationService.getAllNotifications();
        return ResponseEntity.ok(notificationMapper.toDtoList(notifications));
    }

    /**
     * Get notifications for a specific recipient
     */
    @GetMapping("/recipient/{recipient}")
    public ResponseEntity<List<NotificationDto>> getNotificationsByRecipient(@PathVariable String recipient) {
        validateCurrentUserAccess(recipient);
        List<Notification> notifications = notificationService.getNotificationsByRecipient(recipient);
        return ResponseEntity.ok(notificationMapper.toDtoList(notifications));
    }
    
    /**
     * Get unread notifications for a specific recipient
     */
    @GetMapping("/unread/{recipient}")
    public ResponseEntity<List<NotificationDto>> getUnreadNotifications(@PathVariable String recipient) {
        validateCurrentUserAccess(recipient);
        List<Notification> notifications = notificationService.getUnreadNotifications(recipient);
        return ResponseEntity.ok(notificationMapper.toDtoList(notifications));
    }
    
    /**
     * Mark a notification as read
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        Notification notification = findNotificationById(id);
        validateOwnershipOrAdmin(notification);
        notificationService.markNotificationAsRead(id);
        return ResponseEntity.ok().build();
    }
    
    /**
     * Mark all notifications as read for a recipient
     */
    @PutMapping("/read-all/{recipient}")
    public ResponseEntity<Void> markAllAsRead(@PathVariable String recipient) {
        validateCurrentUserAccess(recipient);
        notificationService.markAllNotificationsAsRead(recipient);
        return ResponseEntity.ok().build();
    }

    /**
     * Delete a notification
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        Notification notification = findNotificationById(id);
        validateOwnershipOrAdmin(notification);
        notificationService.deleteNotification(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Helper method to find a notification by ID
     */
    private Notification findNotificationById(Long id) {
        return notificationService.getAllNotifications().stream()
            .filter(n -> n.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", id));
    }
    
    /**
     * Helper method to validate that the current user has access to a recipient's notifications
     */
    private void validateCurrentUserAccess(String recipient) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        if (!username.equals(recipient) && !username.equals("ADMIN")) {
            throw new AccessDeniedException("Access denied");
        }
    }
    
    /**
     * Helper method to validate that the current user is the owner of a notification or an admin
     */
    private void validateOwnershipOrAdmin(Notification notification) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        if (!notification.getRecipient().equals(username) && !username.equals("ADMIN")) {
            throw new AccessDeniedException("Access denied");
        }
    }
} 