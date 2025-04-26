package com.restApi.bankApp.exceptions;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
public class ValidationErrorResponse extends ErrorResponse {
    private final Map<String, String> errors;

    public ValidationErrorResponse(LocalDateTime timestamp, int status, String error, 
                                  String message, Map<String, String> errors) {
        super(timestamp, status, error, message, null);
        this.errors = errors;
    }
} 