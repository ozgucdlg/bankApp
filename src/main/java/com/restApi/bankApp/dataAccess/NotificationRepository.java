package com.restApi.bankApp.dataAccess;

import com.restApi.bankApp.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipient(String recipient);
    List<Notification> findByRecipientAndStatus(String recipient, String status);
    List<Notification> findByStatus(String status);
} 