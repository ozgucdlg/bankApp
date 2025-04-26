package com.restApi.bankApp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {
    @NotBlank(message = "Recipient is required")
    private String recipient;
    
    @NotBlank(message = "Subject is required")
    private String subject;
    
    @NotBlank(message = "Content is required")
    private String content;
} 