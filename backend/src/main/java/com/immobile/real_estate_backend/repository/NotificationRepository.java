package com.immobile.real_estate_backend.repository;

import com.immobile.real_estate_backend.model.entity.Notification;
import com.immobile.real_estate_backend.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Query("SELECT n FROM Notification n WHERE n.user.userId = :userId AND n.isRead = false")
    List<Notification> findUnreadByUserId(@Param("userId") Long userId);

    List<Notification> findAllByUser(User user);
}
