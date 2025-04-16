package com.restApi.bankApp.dataAccess;

import com.restApi.bankApp.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
} 