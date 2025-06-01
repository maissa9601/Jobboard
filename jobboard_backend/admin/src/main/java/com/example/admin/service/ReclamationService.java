package com.example.admin.service;

import com.example.admin.Dto.NotificationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class ReclamationService {

    private final RestTemplate restTemplate;
    private static final String NOTIFICATION_BASE_URL = "http://localhost:8084/notifications";

    @Autowired
    public ReclamationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<NotificationDto> getAllReclamationNotifications() {
        ResponseEntity<NotificationDto[]> response = restTemplate.getForEntity(
                NOTIFICATION_BASE_URL + "/reclamations", NotificationDto[].class);

        NotificationDto[] body = response.getBody();
        return body != null ? Arrays.asList(body) : List.of();
    }
    public void deleteReclamation(Long id) {
        restTemplate.delete(NOTIFICATION_BASE_URL + "/delete/{id}" + id);
    }
}
