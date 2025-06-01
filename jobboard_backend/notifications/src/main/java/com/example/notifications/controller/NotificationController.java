package com.example.notifications.controller;

import com.example.notifications.model.Notification;
import com.example.notifications.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;


    @GetMapping("/reclamations")
    public ResponseEntity<List<Notification>> getAllReclamations() {
        List<Notification> list = notificationService.getReclamationsForAdmin();
        return ResponseEntity.ok(list);
    }


    @GetMapping("/job-alerts/{recipientId}")
    public ResponseEntity<List<Notification>> getJobAlerts(@PathVariable Long candidateId) {
        List<Notification> list = notificationService.getJobAlertsForCandidate(candidateId);
        return ResponseEntity.ok(list);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotificationById(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }


}
