package com.example.admin.service;

import com.example.admin.Dto.JobOffer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class JobService {

    private final RestTemplate restTemplate;
    private final String OFFER_SERVICE_URL = "http://localhost:8081";

    @Autowired
    public JobService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<JobOffer> getAllOffers() {
        ResponseEntity<JobOffer[]> response = restTemplate.getForEntity(OFFER_SERVICE_URL + "/offers", JobOffer[].class);
        return Arrays.asList(response.getBody());
    }

    public JobOffer getOfferById(Long id) {
        return restTemplate.getForObject(OFFER_SERVICE_URL + "/offer/" + id, JobOffer.class);
    }

    public JobOffer createJobOffer(JobOffer jobOffer) {
        return restTemplate.postForObject(OFFER_SERVICE_URL + "/offer/add", jobOffer, JobOffer.class);
    }

    public void updateOffer(Long id, JobOffer offer) {
        restTemplate.put(OFFER_SERVICE_URL + "/offer/" + id, offer);
    }

    public void deleteOffer(Long id) {
        restTemplate.delete(OFFER_SERVICE_URL + "/offer/" + id);
    }

    public List<JobOffer> getMostViewedOffers() {
        ResponseEntity<List<JobOffer>> response = restTemplate.exchange(
                OFFER_SERVICE_URL + "/offer/most-viewed",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<JobOffer>>() {}
        );
        return response.getBody();
    }

    public Map<String, Long> getOfferStats() {
        ResponseEntity<Map<String, Long>> response = restTemplate.exchange(
                OFFER_SERVICE_URL + "/offer/stats",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );
        return response.getBody();
    }
}
