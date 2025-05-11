package com.restApi.bankApp.controllers;

import com.restApi.bankApp.business.abstracts.TransactionService;
import com.restApi.bankApp.business.abstracts.AccountService;
import com.restApi.bankApp.entities.Transaction;
import com.restApi.bankApp.entities.Account;
import com.restApi.bankApp.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AccountService accountService;
    
    @Autowired
    private SecurityUtils securityUtils;

    @PostMapping("/transfer")
    public ResponseEntity<?> transferMoney(@RequestBody Map<String, Object> request) {
        String username = securityUtils.getCurrentUsername();

        int fromAccountId = (Integer) request.get("fromAccountId");
        
        // Verify ownership of source account
        Account fromAccount = accountService.getAccount(fromAccountId)
            .orElseThrow(() -> new RuntimeException("Source account not found"));
            
        if (!fromAccount.getAccountHolderName().equals(username)) {
            return ResponseEntity.status(403).body("Access denied: You can only transfer from your own accounts");
        }

        try {
            int toAccountId = (Integer) request.get("toAccountId");
            double amount = Double.parseDouble(request.get("amount").toString());
            String description = (String) request.get("description");
            
            Transaction transaction = transactionService.transferMoney(
                fromAccountId, toAccountId, amount, description);
            return ResponseEntity.ok(transaction);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<?> getAccountTransactions(@PathVariable int accountId) {
        String username = securityUtils.getCurrentUsername();

        // Verify account ownership
        Account account = accountService.getAccount(accountId)
            .orElseThrow(() -> new RuntimeException("Account not found"));
            
        if (!account.getAccountHolderName().equals(username)) {
            return ResponseEntity.status(403).body("Access denied: You can only view your own transactions");
        }

        List<Transaction> transactions = transactionService.getAccountTransactions(accountId);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<?> getTransaction(@PathVariable Long transactionId) {
        String username = securityUtils.getCurrentUsername();

        try {
            Transaction transaction = transactionService.getTransactionById(transactionId);
            
            // Verify that the user is involved in the transaction
            if (!transaction.getFromAccount().getAccountHolderName().equals(username) &&
                !transaction.getToAccount().getAccountHolderName().equals(username)) {
                return ResponseEntity.status(403).body("Access denied: You can only view your own transactions");
            }
            
            return ResponseEntity.ok(transaction);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
} 