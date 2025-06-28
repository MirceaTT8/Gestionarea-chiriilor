package com.immobile.real_estate_backend.model.dto;

import com.immobile.real_estate_backend.model.enums.NotificationStatus;
import com.immobile.real_estate_backend.model.enums.NotificationType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDTO {
    private Long notificationId;
    private Long userId;
    private String title;
    private String message;
    private NotificationType type;
    private NotificationStatus status;
    private LocalDateTime createdAt;
    private boolean isRead;
}