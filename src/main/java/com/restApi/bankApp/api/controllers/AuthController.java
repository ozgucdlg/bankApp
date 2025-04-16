package com.restApi.bankApp.api.controllers;

import com.restApi.bankApp.business.abstracts.AuthService;
import com.restApi.bankApp.entities.Auth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

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
    public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password) {
        Optional<Auth> auth = authService.login(username, password);
        if (auth.isPresent()) {
            return ResponseEntity.ok(auth.get());
        }
        return ResponseEntity.badRequest().body("Invalid credentials");
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        Optional<Auth> auth = authService.getAuthByUsername(username);
        return auth.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
} 