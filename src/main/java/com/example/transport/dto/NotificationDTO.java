package com.example.transport.dto;

import com.example.transport.entitie.enumeration.NotificationType;

import java.time.LocalDateTime;

public class NotificationDTO {
    private String title;
    private String message;
    private NotificationType type; // ETA_ALARM, PENALTY, etc.
    private LocalDateTime createdAt;

    public NotificationDTO(String title, String message, NotificationType type, LocalDateTime createdAt, Long targetUserId) {
        this.title = title;
        this.message = message;
        this.type = type;
        this.createdAt = createdAt;
        this.targetUserId = targetUserId;
    }

    public NotificationDTO() {
    }

    private Long targetUserId; // ID du parent concerné

    // Constructeurs, Getters & Setters

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(Long targetUserId) {
        this.targetUserId = targetUserId;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
