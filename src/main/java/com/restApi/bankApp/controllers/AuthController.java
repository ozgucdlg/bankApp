package com.restApi.bankApp.controllers;

import com.restApi.bankApp.business.abstracts.AuthService;
import com.restApi.bankApp.business.abstracts.NotificationService;
import com.restApi.bankApp.entities.Auth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        List<Auth> users = authService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Auth auth) {
        try {
            Auth registeredAuth = authService.register(auth);
            
            // Manually trigger a notification creation
            System.out.println("Creating welcome notification for new user: " + auth.getUsername());
            try {
                // Send notification using username as recipient (not email) so it shows up properly
                notificationService.sendNotification(
                    auth.getUsername(), // Use username instead of email
                    "Welcome to Secure Bank", 
                    "Welcome " + auth.getUsername() + "! Your account has been created successfully.\n\n" +
                    "Your account details:\n" +
                    "- Username: " + auth.getUsername() + "\n" +
                    "- Account ID: " + auth.getAccount().getId() + "\n" +
                    "- Initial Balance: $5,000.00\n\n" +
                    "Thank you for choosing our bank!"
                );
                
                // Also create a notification about funding their account
                notificationService.sendNotification(
                    auth.getUsername(),
                    "Fund Your Account",
                    "Dear " + auth.getUsername() + ",\n\n" +
                    "Your new account is ready to use. You can now fund your account by:\n\n" +
                    "1. Bank transfer from another account\n" +
                    "2. Deposit funds at a branch location\n" +
                    "3. Set up direct deposit\n\n" +
                    "Need help? Contact our support team at support@securebank.com."
                );
                
                System.out.println("Welcome notifications created successfully");
            } catch (Exception e) {
                System.out.println("Failed to create welcome notification: " + e.getMessage());
                e.printStackTrace();
            }
            
            return ResponseEntity.ok(registeredAuth);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Auth auth) {
        System.out.println("Login attempt - Username: " + auth.getUsername());  // Debug log
        Optional<Auth> authResult = authService.login(auth.getUsername(), auth.getPassword());
        if (authResult.isPresent()) {
            System.out.println("Login successful for user: " + auth.getUsername());  // Debug log
            return ResponseEntity.ok(authResult.get());
        }
        System.out.println("Login failed for user: " + auth.getUsername());  // Debug log
        return ResponseEntity.badRequest().body("Invalid credentials");
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        Optional<Auth> auth = authService.getAuthByUsername(username);
        return auth.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAuth(@PathVariable int id, @RequestBody Auth auth) {
        try {
            auth.setId(id); // Ensure the ID matches the path variable
            return ResponseEntity.ok(authService.updateAuth(auth));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAuth(@PathVariable int id) {
        try {
            authService.deleteAuth(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        try {
            // Clear security context
            SecurityContextHolder.clearContext();
            
            // Invalidate session if it exists
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            
            return ResponseEntity.ok().body("Logged out successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error during logout: " + e.getMessage());
        }
    }
} 