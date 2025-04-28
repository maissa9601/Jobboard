package com.example.admin.controller;

import com.example.admin.Dto.Admin;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "http://localhost:4200")
public class AdminController {

    private final RestTemplate restTemplate;
    private final String AUTH_SERVICE_URL = "http://localhost:8080/users";

    public AdminController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/admins")
    public List<Admin> getAllAdmins() {
        ResponseEntity<Admin[]> response = restTemplate.getForEntity(AUTH_SERVICE_URL + "/admins", Admin[].class);
        return Arrays.asList(response.getBody());
    }
}
