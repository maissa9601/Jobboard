package com.example.notifications.repository;

import com.example.notifications.model.Notification;
import com.example.notifications.model.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    public List<Notification> findByType(NotificationType type);
    List<Notification> findByRecipientIdAndType(Long recipientId, NotificationType type);
}
