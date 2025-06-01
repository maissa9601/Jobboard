package com.example.admin.service;

import com.example.admin.Dto.Candidat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class CandidatService {

    private final RestTemplate restTemplate;
    private final String AUTH_SERVICE_URL = "http://localhost:8080/users";

    @Autowired
    public CandidatService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Candidat> getAllCandidats() {
        ResponseEntity<Candidat[]> response = restTemplate.getForEntity(AUTH_SERVICE_URL + "/candidats", Candidat[].class);
        Candidat[] body = response.getBody();
        return body != null ? Arrays.asList(body) : Collections.emptyList();
    }

    public Candidat promote(Long id) {
        ResponseEntity<Candidat> response = restTemplate.postForEntity(AUTH_SERVICE_URL + "/candidats/" + id + "/promote", null, Candidat.class);
        return response.getBody();
    }

    public void deleteCandidat(Long id) {
        restTemplate.delete(AUTH_SERVICE_URL + "/candidats/" + id);
    }

    public Map<String, Long> getUserStats() {
        ResponseEntity<Map<String, Long>> response = restTemplate.exchange(
                AUTH_SERVICE_URL + "/stats",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, Long>>() {}
        );
        return response.getBody() != null ? response.getBody() : Collections.emptyMap();
    }

    public List<Candidat> getRecentCandidats() {
        ResponseEntity<Candidat[]> response = restTemplate.getForEntity(
                AUTH_SERVICE_URL + "/candidats/recent",
                Candidat[].class
        );
        Candidat[] body = response.getBody();
        return body != null ? Arrays.asList(body) : Collections.emptyList();
    }
}
