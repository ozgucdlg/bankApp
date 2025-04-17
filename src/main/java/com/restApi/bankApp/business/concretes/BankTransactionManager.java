package com.restApi.bankApp.business.concretes;

import com.restApi.bankApp.business.abstracts.TransactionService;
import com.restApi.bankApp.business.abstracts.AccountService;
import com.restApi.bankApp.business.abstracts.NotificationService;
import com.restApi.bankApp.dataAccess.TransactionRepository;
import com.restApi.bankApp.dataAccess.AccountRepository;
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
    private AccountRepository accountRepository;

    @Autowired
    private AccountService accountService;

    @Autowired
    private NotificationService notificationService;

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
            String insufficientFundsMessage = String.format(
                "Transaction failed: Insufficient funds. Available balance: %.2f, Attempted transfer: %.2f",
                fromAccount.getBalance(), amount
            );
            notificationService.sendNotification(
                fromAccount.getAccountHolderName(),
                "Transaction Failed",
                insufficientFundsMessage
            );
            throw new RuntimeException("Insufficient funds");
        }

        Transaction transaction = new Transaction(fromAccount, toAccount, amount, description);

        try {
            // Perform the transfer
            fromAccount.setBalance(fromAccount.getBalance() - amount);
            toAccount.setBalance(toAccount.getBalance() + amount);

            // Save account changes directly through repository to avoid security checks
            accountRepository.save(fromAccount);
            accountRepository.save(toAccount);

            // Update transaction status
            transaction.setStatus(TransactionStatus.COMPLETED);
            Transaction savedTransaction = transactionRepository.save(transaction);

            // Send success notifications to both parties
            String senderNotification = String.format(
                "Transfer completed successfully!\nAmount: %.2f\nTo: %s\nNew Balance: %.2f\nTransaction ID: %d",
                amount, toAccount.getAccountHolderName(), fromAccount.getBalance(), savedTransaction.getId()
            );
            notificationService.sendNotification(
                fromAccount.getAccountHolderName(),
                "Transfer Successful",
                senderNotification
            );

            // Notify recipient
            String recipientNotification = String.format(
                "You have received a transfer!\nAmount: %.2f\nFrom: %s\nNew Balance: %.2f\nTransaction ID: %d",
                amount, fromAccount.getAccountHolderName(), toAccount.getBalance(), savedTransaction.getId()
            );
            notificationService.sendNotification(
                toAccount.getAccountHolderName(),
                "Transfer Received",
                recipientNotification
            );

            return savedTransaction;
        } catch (Exception e) {
            // Set transaction status to FAILED
            transaction.setStatus(TransactionStatus.FAILED);
            transactionRepository.save(transaction);

            // Notify both parties about the failure
            String errorMessage = String.format(
                "Transaction failed!\nAmount: %.2f\nError: %s\nTransaction ID: %d",
                amount, e.getMessage(), transaction.getId()
            );

            // Notify sender
            notificationService.sendNotification(
                fromAccount.getAccountHolderName(),
                "Transaction Failed",
                errorMessage
            );

            // Notify recipient
            notificationService.sendNotification(
                toAccount.getAccountHolderName(),
                "Transaction Failed",
                errorMessage
            );

            throw new RuntimeException("Transaction failed: " + e.getMessage());
        }
    }

    @Override
    public List<Transaction> getAccountTransactions(int accountId) {
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