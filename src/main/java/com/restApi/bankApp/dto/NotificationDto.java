package com.restApi.bankApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {
    private Long id;
    private String recipient;
    private String subject;
    private String content;
    private String notificationType;
    private LocalDateTime sentAt;
    private String status;
    private Boolean read = false;
} 