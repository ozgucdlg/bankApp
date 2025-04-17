package com.restApi.bankApp.business.abstracts;

import com.restApi.bankApp.entities.Account;
import java.util.Optional;

/**
 * Internal service for managing bank accounts.
 * This service should not be exposed directly through REST endpoints.
 * Instead, it should be used internally by other services like AuthService and TransactionService.
 */
public interface AccountService {
    /**
     * Creates a new account. This method should only be called from AuthService during registration.
     * Direct calls from other services or controllers will result in SecurityException.
     * @param account The account to create
     * @param callingClass The class name of the caller (for security validation)
     * @return The created account
     * @throws SecurityException if called from unauthorized service
     * @throws IllegalArgumentException if account holder name is null/empty or initial balance is negative
     */
    Account createAccount(Account account, String callingClass);
    
    /**
     * Retrieves an account by ID.
     * @param id The account ID
     * @return Optional containing the account if found, empty otherwise
     */
    Optional<Account> getAccount(int id);
    
    /**
     * Deposits money into an account.
     * @param id The account ID
     * @param amount The amount to deposit (must be positive)
     * @return The updated account
     * @throws IllegalArgumentException if amount is not positive
     * @throws RuntimeException if account is not found
     */
    Account deposit(int id, double amount);
    
    /**
     * Withdraws money from an account.
     * @param id The account ID
     * @param amount The amount to withdraw (must be positive)
     * @return The updated account
     * @throws IllegalArgumentException if amount is not positive or if insufficient funds
     * @throws RuntimeException if account is not found
     */
    Account withdraw(int id, double amount);
}
