package com.example.notifications.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "notifications")
@AllArgsConstructor
@Builder
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long recipientId;
    private String recipientEmail;
    private String senderEmail;
    private Long senderId;
    private String subject;
    private String content;
    private LocalDateTime timestamp;
    private boolean isRead;

    @Enumerated(EnumType.STRING)
    private NotificationType type;
}
