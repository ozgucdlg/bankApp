package com.restApi.bankApp.controller;

import com.restApi.bankApp.business.abstracts.TransactionService;
import com.restApi.bankApp.entities.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/transfer")
    public Transaction transferMoney(@RequestBody Map<String, Object> request) {
        int fromAccountId = (Integer) request.get("fromAccountId");
        int toAccountId = (Integer) request.get("toAccountId");
        double amount = Double.parseDouble(request.get("amount").toString());
        String description = (String) request.get("description");
        
        return transactionService.transferMoney(fromAccountId, toAccountId, amount, description);
    }

    @GetMapping("/account/{accountId}")
    public List<Transaction> getAccountTransactions(@PathVariable int accountId) {
        return transactionService.getAccountTransactions(accountId);
    }

    @GetMapping("/{transactionId}")
    public Transaction getTransaction(@PathVariable Long transactionId) {
        return transactionService.getTransactionById(transactionId);
    }
} 