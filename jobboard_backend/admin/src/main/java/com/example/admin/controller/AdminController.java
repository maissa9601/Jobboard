package com.example.admin.controller;

import com.example.admin.Dto.AdminUpdateRequest;
import com.example.admin.model.Admin;
import com.example.admin.service.AdmineService;
import com.example.admin.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import java.util.Map;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "http://localhost:4200")
public class AdminController {

    private final AdmineService adminService;
     private final RestTemplate restTemplate;
    private final String AUTH_SERVICE_URL = "http://localhost:8080/users";

    @Autowired
    public AdminController(AdmineService adminService, RestTemplate restTemplate) {
        this.adminService = adminService;
        this.restTemplate = restTemplate;
    }
    //api from micro auth
    @GetMapping("/admins")
    public ResponseEntity<List<Admin>> getAllAdmins() {

        ResponseEntity<Admin[]> response = restTemplate.getForEntity(AUTH_SERVICE_URL + "/admins", Admin[].class);
        Admin[] body = response.getBody();

        if (body == null) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        return ResponseEntity.ok(Arrays.asList(body));
    }
    //his own api

    @PostMapping("/profile/create")
    public ResponseEntity<Admin> createProfile() {
        Long userId = extractUserIdFromToken();
        String email = extractEmailFromToken();
        Admin saved = adminService.createProfile(userId, email);
        return ResponseEntity.ok(saved);
    }
    @GetMapping("/profile/me")
    public ResponseEntity<Admin> getMyProfile() {
        Long userId = extractUserIdFromToken();
        return adminService.getProfile(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping(value = "/profile/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProfile(@ModelAttribute AdminUpdateRequest request) {
        Long userId = extractUserIdFromToken();
        adminService.updateProfile(userId, request);
        return ResponseEntity.ok(Map.of("message", "Profile updated successfully"));
    }

    @PostMapping("/profile/photo/upload")
    public ResponseEntity<String> uploadPhoto(@RequestParam("file") MultipartFile file) {
        Long userId = extractUserIdFromToken();
        String photoUrl = adminService.uploadPhoto(userId, file);
        return ResponseEntity.ok(photoUrl);
    }


    private Long extractUserIdFromToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal user) {
            return user.getUserId();
        }
        return null;
    }

    private String extractEmailFromToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal user) {
            return user.getEmail();
        }
        return null;
    }
}
