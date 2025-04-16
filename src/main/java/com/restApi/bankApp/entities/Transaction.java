package com.restApi.bankApp.entities;

import com.restApi.bankApp.enums.TransactionStatus;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name="transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "from_account_id")
    private Account fromAccount;

    @ManyToOne
    @JoinColumn(name = "to_account_id")
    private Account toAccount;

    @Column(name="amount", nullable = false)
    private Double amount;

    @Column(name="timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name="description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name="status")
    private TransactionStatus status;

    // Constructor for convenience
    public Transaction(Account fromAccount, Account toAccount, Double amount, String description) {
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
        this.description = description;
        this.timestamp = LocalDateTime.now();
        this.status = TransactionStatus.PENDING;
    }

    public Transaction() {
        this.timestamp = LocalDateTime.now();
        this.status = TransactionStatus.PENDING;
    }
} 