package com.restApi.bankApp.config;

import com.restApi.bankApp.business.abstracts.NotificationService;
import com.restApi.bankApp.dataAccess.NotificationRepository;
import com.restApi.bankApp.entities.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
public class DataSeeder {

    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private UserDetailsService userDetailsService;
    
    @Bean
    public CommandLineRunner seedNotifications() {
        return args -> {
            // Check if we need to seed notifications
            long count = notificationRepository.count();
            if (count == 0) {
                System.out.println("Seeding test notifications...");
                
                // Create sample notifications for admin user
                notificationService.sendNotification(
                    "admin",
                    "Welcome to Secure Bank",
                    "Thank you for using our banking application. This is a welcome notification."
                );
                
                notificationService.sendNotification(
                    "admin",
                    "Security Alert",
                    "Your account was accessed from a new device. If this wasn't you, please contact support."
                );
                
                notificationService.sendNotification(
                    "admin",
                    "Transfer Successful",
                    "Your transfer of $1000 to Account #12345 has been completed successfully."
                );
                
                // Set the first two as read
                List<Notification> adminNotifications = notificationRepository.findByRecipient("admin");
                if (adminNotifications.size() >= 2) {
                    adminNotifications.get(0).setRead(true);
                    adminNotifications.get(1).setRead(true);
                    notificationRepository.saveAll(adminNotifications);
                }
                
                System.out.println("Notification seeding complete!");
            } else {
                System.out.println("Existing notifications found, skipping seeding.");
            }
        };
    }
} 