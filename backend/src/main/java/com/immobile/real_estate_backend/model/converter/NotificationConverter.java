package com.immobile.real_estate_backend.model.converter;

import com.immobile.real_estate_backend.model.dto.NotificationDTO;
import com.immobile.real_estate_backend.model.entity.Notification;
import com.immobile.real_estate_backend.model.entity.User;
import com.immobile.real_estate_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationConverter {

    private final UserRepository userRepository;

    public NotificationDTO toNotificationDTO(Notification notification) {
        return NotificationDTO.builder()
                .notificationId(notification.getNotificationId())
                .userId(notification.getUser() != null ? notification.getUser().getUserId() : null)
                .title(notification.getTitle())
                .message(notification.getMessage())
                .type(notification.getType())
                .status(notification.getStatus())
                .createdAt(notification.getCreatedAt())
                .isRead(notification.isRead())
                .build();
    }

    public Notification toNotification(NotificationDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + dto.getUserId()));

        return Notification.builder()
                .notificationId(dto.getNotificationId())
                .user(user)
                .title(dto.getTitle())
                .message(dto.getMessage())
                .type(dto.getType())
                .status(dto.getStatus())
                .createdAt(dto.getCreatedAt())
                .isRead(dto.isRead())
                .build();
    }
}
