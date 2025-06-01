package com.example.notifications.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReclamationEvent {
    private Long senderId;
    private String senderEmail;
    private String subject;
    private String message;
}
