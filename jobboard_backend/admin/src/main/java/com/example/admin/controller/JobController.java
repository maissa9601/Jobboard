package com.example.admin.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.admin.Dto.JobOffer;
import org.springframework.web.client.RestTemplate;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "http://localhost:4200")
public class JobController {

    private final RestTemplate restTemplate;
    private final String OFFER_SERVICE_URL = "http://localhost:8081";

    public JobController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/offers")
    public List<JobOffer> getAllOffers() {
        ResponseEntity<JobOffer[]> response = restTemplate.getForEntity(OFFER_SERVICE_URL + "/offers", JobOffer[].class);
        return Arrays.asList(response.getBody());
    }

    @PostMapping("/offer")
    public JobOffer createJobOffer(@RequestBody JobOffer jobOffer) {
        return restTemplate.postForObject(OFFER_SERVICE_URL + "/offer", jobOffer, JobOffer.class);
    }

    @GetMapping("/offers/{id}")
    public JobOffer getOfferById(@PathVariable Long id) {
        return restTemplate.getForObject(OFFER_SERVICE_URL + "/offer/" + id, JobOffer.class);
    }

    @PutMapping("/offer/{id}")
    public void updateOffer(@PathVariable Long id, @RequestBody JobOffer offer) {
        restTemplate.put(OFFER_SERVICE_URL + "/offer/" + id, offer);
    }

    @DeleteMapping("/offer/{id}")
    public void deleteOffer(@PathVariable Long id) {
        restTemplate.delete(OFFER_SERVICE_URL + "/offer/" + id);
    }
}
