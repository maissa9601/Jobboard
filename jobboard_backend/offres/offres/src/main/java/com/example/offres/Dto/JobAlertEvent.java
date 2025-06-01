package com.example.offres.Dto;


import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class JobAlertEvent {
    private Long recipientId;
    private String recipientEmail;
    private String title;
    private String description;


}
