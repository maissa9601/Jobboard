package com.example.admin.Dto;
import lombok.Data;



@Data
public class NotificationDto {
    Long id;
    String content;
    String senderEmail;
    String subject;
}
