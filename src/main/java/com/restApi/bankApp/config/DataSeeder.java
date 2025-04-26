package com.restApi.bankApp.config;

import com.restApi.bankApp.business.abstracts.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * This class seeds initial data into the database
 * For Docker compatibility, we'll disable it temporarily
 */
@Configuration
public class DataSeeder {
    
    @Autowired
    private NotificationService notificationService;
    
    /**
     * Commented out to prevent startup errors in Docker
     * Uncomment when schema issues are resolved
     */
    //@Bean
    //@Order(1)
    public CommandLineRunner seedNotifications() {
        return args -> {
            // Check if there are existing notifications
            long count = notificationService.getAllNotifications().size();
            
            if (count == 0) {
                System.out.println("Seeding test notifications...");
                
                // Create a welcome notification
                notificationService.sendNotification(
                    "admin",
                    "Welcome to Secure Bank",
                    "Thank you for using our banking application. This is a welcome notification."
                );
            }
        };
    }
} 