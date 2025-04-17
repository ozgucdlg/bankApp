package com.restApi.bankApp.business.concretes;

import com.restApi.bankApp.business.abstracts.AccountService;
import com.restApi.bankApp.dataAccess.AccountRepository;
import com.restApi.bankApp.entities.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
class AccountManager implements AccountService {  // Package-private class

    private static final String AUTH_MANAGER_CLASS = "com.restApi.bankApp.business.concretes.AuthManager";

    @Autowired
    private AccountRepository accountRepository;

    @Override
    @Transactional
    public Account createAccount(Account account, String callingClass) {
        // Validate calling context
        if (!AUTH_MANAGER_CLASS.equals(callingClass)) {
            throw new SecurityException("Account creation is only allowed through the authentication service");
        }

        // Validate account data
        if (account.getAccountHolderName() == null || account.getAccountHolderName().trim().isEmpty()) {
            throw new IllegalArgumentException("Account holder name is required");
        }
        
        if (account.getBalance() < 0) {
            throw new IllegalArgumentException("Initial balance cannot be negative");
        }
        
        return accountRepository.save(account);
    }

    @Override
    public Optional<Account> getAccount(int id) {
        return accountRepository.findById(id);
    }

    @Override
    @Transactional
    public Account deposit(int id, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }

        Account account = getAccount(id)
            .orElseThrow(() -> new RuntimeException("Account not found"));
            
        account.setBalance(account.getBalance() + amount);
        return accountRepository.save(account);
    }

    @Override
    @Transactional
    public Account withdraw(int id, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }

        Account account = getAccount(id)
            .orElseThrow(() -> new RuntimeException("Account not found"));
            
        if (account.getBalance() < amount) {
            throw new IllegalArgumentException(
                String.format("Insufficient funds. Available: %.2f, Requested: %.2f",
                    account.getBalance(), amount)
            );
        }
        
        account.setBalance(account.getBalance() - amount);
        return accountRepository.save(account);
    }
}
