package com.example.notifications.service;



import com.example.notifications.dto.NotificationRequest;
import com.example.notifications.dto.ReclamationEvent;
import com.example.notifications.model.Notification;
import com.example.notifications.model.NotificationType;
import com.example.notifications.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository repository;
    private final EmailService emailService;

    @KafkaListener(
            topics = "new-job-alerts",
            groupId = "notification-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void JobAlert(NotificationRequest event) {
        Notification notification = Notification.builder()
                .recipientEmail(event.getRecipientEmail())
                .recipientId(event.getRecipientId())
                .subject("New job offer: " + event.getSubject())
                .content(event.getContent())
                .timestamp(LocalDateTime.now())
                .type(NotificationType.JOB_ALERT)
                .isRead(false)
                .build();

        repository.save(notification);


        emailService.sendEmail(
                event.getRecipientEmail(),
                "New job matching your preferences!",
                event.getContent()
        );
    }
    @KafkaListener(
            topics = "reclamation",
            groupId = "notification-group",containerFactory = "reclamationKafkaListenerContainerFactory"
    )
    public void handleReclamation(ReclamationEvent event) {
        Notification notification = Notification.builder()
                .senderId(event.getSenderId())
                .senderEmail(event.getSenderEmail())
                .subject(event.getSubject())
                .content("Reclamation: " + event.getMessage())
                .timestamp(LocalDateTime.now())
                .type(NotificationType.RECLAMATION)
                .isRead(false)
                .build();

        repository.save(notification);

        emailService.sendEmail(
                event.getSenderEmail(),
                "We received your reclamation",
                "Thank you for your feedback. Here is a copy of your message: " + event.getMessage()
        );
    }
    public List<Notification> getReclamationsForAdmin() {
        return repository.findByType(NotificationType.RECLAMATION);
    }

    public List<Notification> getJobAlertsForCandidate(Long candidateId) {
        return repository.findByRecipientIdAndType(candidateId, NotificationType.JOB_ALERT);
    }
    public void deleteNotificationById(Long id) {
        if (repository.existsById(id)) {
           repository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Notification non trouvée avec l'id : " + id);
        }
    }


}

