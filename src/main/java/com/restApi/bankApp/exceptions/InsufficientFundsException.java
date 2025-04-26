package com.restApi.bankApp.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(String message) {
        super(message);
    }

    public InsufficientFundsException(Long accountId, double requested, double available) {
        super(String.format("Account %d has insufficient funds. Requested: %.2f, Available: %.2f", 
               accountId, requested, available));
    }
} 