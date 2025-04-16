package com.restApi.bankApp.business.concretes;

import com.restApi.bankApp.business.abstracts.TransactionService;
import com.restApi.bankApp.business.abstracts.AccountService;
import com.restApi.bankApp.dataAccess.TransactionRepository;
import com.restApi.bankApp.entities.Account;
import com.restApi.bankApp.entities.Transaction;
import com.restApi.bankApp.enums.TransactionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BankTransactionManager implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountService accountService;

    @Override
    @Transactional
    public Transaction transferMoney(int fromAccountId, int toAccountId, double amount, String description) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }

        Account fromAccount = accountService.getAccount(fromAccountId)
                .orElseThrow(() -> new RuntimeException("Source account not found"));
        Account toAccount = accountService.getAccount(toAccountId)
                .orElseThrow(() -> new RuntimeException("Destination account not found"));

        if (fromAccount.getBalance() < amount) {
            throw new RuntimeException("Insufficient funds");
        }

        Transaction transaction = new Transaction(fromAccount, toAccount, amount, description);

        try {
            // Perform the transfer
            fromAccount.setBalance(fromAccount.getBalance() - amount);
            toAccount.setBalance(toAccount.getBalance() + amount);

            // Save account changes
            accountService.createAccount(fromAccount);
            accountService.createAccount(toAccount);

            // Update transaction status
            transaction.setStatus(TransactionStatus.COMPLETED);
            return transactionRepository.save(transaction);
        } catch (Exception e) {
            transaction.setStatus(TransactionStatus.FAILED);
            transactionRepository.save(transaction);
            throw new RuntimeException("Transaction failed: " + e.getMessage());
        }
    }

    @Override
    public List<Transaction> getAccountTransactions(int accountId) {
        Account account = accountService.getAccount(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        
        return transactionRepository.findAll().stream()
                .filter(t -> t.getFromAccount().getId() == accountId || t.getToAccount().getId() == accountId)
                .collect(Collectors.toList());
    }

    @Override
    public Transaction getTransactionById(Long transactionId) {
        return transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
    }
} 