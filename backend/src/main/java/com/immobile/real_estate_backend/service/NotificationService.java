package com.immobile.real_estate_backend.service;

import com.immobile.real_estate_backend.model.converter.NotificationConverter;
import com.immobile.real_estate_backend.model.dto.NotificationDTO;
import com.immobile.real_estate_backend.model.entity.Notification;
import com.immobile.real_estate_backend.model.entity.User;
import com.immobile.real_estate_backend.model.enums.NotificationStatus;
import com.immobile.real_estate_backend.repository.NotificationRepository;
import com.immobile.real_estate_backend.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationConverter notificationConverter;
    private final UserRepository userRepository;

    public void createNotification(Notification notification) {

        notificationRepository.save(notification);
    }

    public NotificationDTO createNotification(NotificationDTO notificationDTO) {
        Notification notification = notificationConverter.toNotification(notificationDTO);
        notificationRepository.save(notification);
        return notificationDTO;
    }

    @Transactional
    public NotificationDTO markNotificationAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + notificationId));

        notification.setRead(true);
        notification.setStatus(NotificationStatus.READ);

        Notification updatedNotification = notificationRepository.save(notification);
        return notificationConverter.toNotificationDTO(updatedNotification);
    }

    @Transactional
    public int markAllNotificationsAsReadByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        return markAllNotificationsAsRead(user.getUserId());
    }

    @Transactional
    public int markAllNotificationsAsRead(Long userId) {

        List<Notification> unreadNotifications = notificationRepository
                .findUnreadByUserId(userId);

        unreadNotifications.forEach(notification -> {
            notification.setRead(true);
            notification.setStatus(NotificationStatus.READ);
        });

        notificationRepository.saveAll(unreadNotifications);
        return unreadNotifications.size();
    }

    public Notification getNotification(Long id) {
        return notificationRepository.findById(id).orElse(null);
    }

    public List<NotificationDTO> getNotificationsByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        List<Notification> notifications = notificationRepository.findAllByUser(user);

        return notifications.stream()
                .map(notificationConverter::toNotificationDTO)
                .collect(Collectors.toList());
    }


    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    public List<NotificationDTO> getAllNotificationsDTO() {
        return getAllNotifications().stream()
                .map(notificationConverter::toNotificationDTO)
                .collect(Collectors.toList());
    }


    public NotificationDTO getNotificationDTO(Long id) {
        Notification notification = getNotification(id);
        return notification != null ? notificationConverter.toNotificationDTO(notification) : null;
    }


    public void deleteNotificationById(Long id) {
        notificationRepository.deleteById(id);
    }
}
