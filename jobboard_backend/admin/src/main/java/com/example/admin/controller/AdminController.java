package com.example.admin.controller;

import com.example.admin.Dto.Admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "http://localhost:4200")
public class AdminController {

    private final RestTemplate restTemplate;
    private final String AUTH_SERVICE_URL = "http://localhost:8080/users";

    @Autowired
    public AdminController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/admins")
    public ResponseEntity<List<Admin>> getAllAdmins() {
        ResponseEntity<Admin[]> response = restTemplate.getForEntity(AUTH_SERVICE_URL + "/admins", Admin[].class);
        Admin[] body = response.getBody();

        if (body == null) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        return ResponseEntity.ok(Arrays.asList(body));
    }

}
