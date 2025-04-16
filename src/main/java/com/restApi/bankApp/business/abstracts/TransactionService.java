package com.restApi.bankApp.business.abstracts;

import com.restApi.bankApp.entities.Transaction;
import java.util.List;

public interface TransactionService {
    Transaction transferMoney(int fromAccountId, int toAccountId, double amount, String description);
    List<Transaction> getAccountTransactions(int accountId);
    Transaction getTransactionById(Long transactionId);
} 