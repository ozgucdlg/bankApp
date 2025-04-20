package com.restApi.bankApp.controllers;

import com.restApi.bankApp.business.abstracts.AuthService;
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

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        List<Auth> users = authService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Auth auth) {
        try {
            Auth registeredAuth = authService.register(auth);
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