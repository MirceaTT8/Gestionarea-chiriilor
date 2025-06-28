package com.immobile.real_estate_backend.controller;

import com.immobile.real_estate_backend.model.dto.NotificationDTO;
import com.immobile.real_estate_backend.model.entity.Notification;
import com.immobile.real_estate_backend.model.entity.User;
import com.immobile.real_estate_backend.service.NotificationService;
import com.immobile.real_estate_backend.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/notification")
@AllArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<NotificationDTO>> getAllNotifications() {
        return new ResponseEntity<>(notificationService.getAllNotificationsDTO(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<NotificationDTO> addNotification(@RequestBody NotificationDTO notificationDTO) {
        NotificationDTO created = notificationService.createNotification(notificationDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{notificationId}")
    public ResponseEntity<NotificationDTO> getNotification(@PathVariable("notificationId") Long notificationId) {
        return new ResponseEntity<>(notificationService.getNotificationDTO(notificationId), HttpStatus.OK);
    }

    @GetMapping("/user/email/{email}")
    public ResponseEntity<List<NotificationDTO>> getByUserEmail(@PathVariable String email) {
        return new ResponseEntity<>(notificationService.getNotificationsByEmail(email),HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationDTO>> getUserNotifications(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getAllNotificationsDTO().stream()
                .filter(n -> n.getUserId().equals(userId))
                .collect(Collectors.toList()));
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<NotificationDTO> markAsRead(@PathVariable Long notificationId) {
        return ResponseEntity.ok(notificationService.markNotificationAsRead(notificationId));
    }

    @PatchMapping("/user/{userId}/read-all")
    public ResponseEntity<Integer> markAllAsRead(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.markAllNotificationsAsRead(userId));
    }

    @PatchMapping("/user/email/{email}/read-all")
    public ResponseEntity<Integer> markAllAsReadByEmail(@PathVariable String email) {
        return ResponseEntity.ok(notificationService.markAllNotificationsAsReadByEmail(email));
    }

    @DeleteMapping("{notificationId}")
    public void deleteNotification(@PathVariable Long notificationId) {
        notificationService.deleteNotificationById(notificationId);
    }
}
