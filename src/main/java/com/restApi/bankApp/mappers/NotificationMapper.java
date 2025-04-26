package com.restApi.bankApp.mappers;

import com.restApi.bankApp.dto.NotificationDto;
import com.restApi.bankApp.entities.Notification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class NotificationMapper {

    public NotificationDto toDto(Notification entity) {
        if (entity == null) {
            return null;
        }
        
        NotificationDto dto = new NotificationDto();
        dto.setId(entity.getId());
        dto.setRecipient(entity.getRecipient());
        dto.setSubject(entity.getSubject());
        dto.setContent(entity.getContent());
        dto.setNotificationType(entity.getNotificationType());
        dto.setSentAt(entity.getSentAt());
        dto.setStatus(entity.getStatus());
        dto.setRead(entity.getRead());
        
        return dto;
    }

    public List<NotificationDto> toDtoList(List<Notification> entities) {
        if (entities == null) {
            return List.of();
        }
        
        return entities.stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }

    public Notification toEntity(NotificationDto dto) {
        if (dto == null) {
            return null;
        }
        
        Notification entity = new Notification();
        entity.setId(dto.getId());
        entity.setRecipient(dto.getRecipient());
        entity.setSubject(dto.getSubject());
        entity.setContent(dto.getContent());
        entity.setNotificationType(dto.getNotificationType());
        entity.setSentAt(dto.getSentAt());
        entity.setStatus(dto.getStatus());
        entity.setRead(dto.getRead());
        
        return entity;
    }
} 