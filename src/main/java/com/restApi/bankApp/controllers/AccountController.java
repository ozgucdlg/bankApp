package com.restApi.bankApp.controllers;

import com.restApi.bankApp.business.abstracts.AccountService;
import com.restApi.bankApp.entities.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getAccount(@PathVariable int id) {
        // Get authenticated user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        return accountService.getAccount(id)
            .map(account -> {
                // Check if the authenticated user owns this account
                if (!account.getAccountHolderName().equals(username)) {
                    return ResponseEntity.status(403).body("Access denied");
                }
                return ResponseEntity.ok(account);
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/deposit")
    public ResponseEntity<?> deposit(@PathVariable int id, @RequestBody Map<String, Double> request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        try {
            Account account = accountService.getAccount(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));
                
            if (!account.getAccountHolderName().equals(username)) {
                return ResponseEntity.status(403).body("Access denied");
            }
            
            Double amount = request.get("amount");
            return ResponseEntity.ok(accountService.deposit(id, amount));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/withdraw")
    public ResponseEntity<?> withdraw(@PathVariable int id, @RequestBody Map<String, Double> request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        try {
            Account account = accountService.getAccount(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));
                
            if (!account.getAccountHolderName().equals(username)) {
                return ResponseEntity.status(403).body("Access denied");
            }
            
            Double amount = request.get("amount");
            return ResponseEntity.ok(accountService.withdraw(id, amount));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
} 