package com.example.admin.controller;

import com.example.admin.Dto.Candidat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "http://localhost:4200")
public class CandidatController {

    private final RestTemplate restTemplate;
    private final String AUTH_SERVICE_URL = "http://localhost:8080/users";

    @Autowired
    public CandidatController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/candidats")
    public ResponseEntity<List<Candidat>> getAllCandidats() {
        ResponseEntity<Candidat[]> response = restTemplate.getForEntity(AUTH_SERVICE_URL + "/candidats", Candidat[].class);
        Candidat[] body = response.getBody();

        if (body == null) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        return ResponseEntity.ok(Arrays.asList(body));
    }

    @PostMapping("/candidats/{id}/promote")
    public ResponseEntity<Candidat> promote(@PathVariable Long id) {
        return restTemplate.postForEntity(AUTH_SERVICE_URL + "/candidats/" + id + "/promote", null, Candidat.class);
    }

    @DeleteMapping("/candidats/{id}")
    public void deleteCandidat(@PathVariable Long id) {
        restTemplate.delete(AUTH_SERVICE_URL + "/candidats/" + id);
    }
}
